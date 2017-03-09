package mdp.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import mdp.common.Vector2;
import mdp.map.Descriptor;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.solver.exploration.CalibrationType;

public class Translator implements ITranslatable {

    private static final String _TO_ARDUINO_MARKER = "a";
    private static final String _TO_ANDROID_MARKER = "b";

    private static final String _MOVE_FORWARD_STR = "f";
    private static final String _MOVE_BACKWARD_STR = "b";
    private static final String _ROTATE_LEFT_STR = "l";
    private static final String _ROTATE_RIGHT_STR = "r";

    private static final String _SENSING_REQUEST = "s";
    private static final String _TRAILER = "|";

    private static final String _DESC_SEPARATOR = "g";
    private static final String _MSG_SEPARATOR = "_";

    private static final String _CAL_FRONT_LR = "x";
    private static final String _CAL_FRONT_ML = "y";
    private static final String _CAL_FRONT_MR = "z";
    private static final String _CAL_RIGHT = "c";

    private static final int _PROBING_PERIOD = 200;

    private SocketCommunicator _socketCommunicator;
    private String _inputBuffer;

    public Translator() throws IOException {
        _inputBuffer = "";
    }

    @Override
    public void connect(Runnable callback) {
        _socketCommunicator = new SocketCommunicator(callback);
    }

    @Override
    public String getInputBuffer() {
        return _inputBuffer;
    }

    private String _compileActions(List<RobotAction> actions) {
        String result = "";
        int count = 1;
        String lastAction = "";
        for (RobotAction action : actions) {
            String nextActionStr;
            switch (action) {
                case MoveForward:
                    nextActionStr = _MOVE_FORWARD_STR;
                    break;
                case MoveBackward:
                    nextActionStr = _MOVE_BACKWARD_STR;
                    break;
                case RotateLeft:
                    nextActionStr = _ROTATE_LEFT_STR;
                    break;
                case RotateRight:
                    nextActionStr = _ROTATE_RIGHT_STR;
                    break;
                default:
                    nextActionStr = " ";
                    break;
            }
            if (result.length() != 0) {
                lastAction = Character.toString(result.charAt(result.length() - 1));
                if (lastAction.equals(nextActionStr)) {
                    boolean isRotating = lastAction.equals(_ROTATE_LEFT_STR) || lastAction.equals(_ROTATE_RIGHT_STR);
                    if (isRotating) {
                        result += _TRAILER + lastAction;
                    } else {
                        count++;
                    }
                } else {
                    boolean isRotating = lastAction.equals(_ROTATE_LEFT_STR) || lastAction.equals(_ROTATE_RIGHT_STR);
                    result += (isRotating ? "" : count) + _TRAILER + nextActionStr;
                    count = 1;
                }
            } else {
                result += nextActionStr;
            }
        }
        boolean isRotating;
        if (result.length() != 1) {
            isRotating = lastAction.equals(_ROTATE_LEFT_STR) || lastAction.equals(_ROTATE_RIGHT_STR);
        } else {
            isRotating = result.equals(_ROTATE_LEFT_STR) || result.equals(_ROTATE_RIGHT_STR);
        }

        result += (isRotating ? "" : count) + _TRAILER;
        System.out.println("Sending out: " + result);
        return result;
    }

    private String _compileActions(Map map, List<Vector2> path) {
        String result = "";
        Vector2 lastSavedPos = null;
        Vector2 prevPos = null;
        List<Vector2> smoothPath = new ArrayList<>();
        for (Vector2 curPos : path) {
            if (lastSavedPos == null) {
                lastSavedPos = curPos;
                smoothPath.add(curPos);
            } else {
                // check existence of obstacle in rectangular area
                int minI = Integer.min(lastSavedPos.i(), curPos.i());
                int maxI = Integer.max(lastSavedPos.i(), curPos.i());
                int minJ = Integer.min(lastSavedPos.j(), curPos.j());
                int maxJ = Integer.max(lastSavedPos.j(), curPos.j());
                boolean obsDetected = false;
                for (int i = minI; i <= maxI; i++) {
                    for (int j = minJ; j < maxJ; j++) {
                        if (!map.getPoint(new Vector2(i, j)).obstacleState()
                                .equals(WPObstacleState.IsWalkable)) {
                            obsDetected = true;
                            break;
                        }
                    }
                    if (obsDetected) {
                        break;
                    }
                }
                // if there is, save the prev pos
                if (obsDetected) {
                    smoothPath.add(prevPos);
                    lastSavedPos = prevPos;
                }
            }
            prevPos = curPos;
        }
        // save goal
        smoothPath.add(prevPos);
        double orientation;
        switch (new Robot().orientation()) {
            case Up:
                orientation = 90;
                break;
            case Down:
                orientation = 270;
                break;
            case Left:
                orientation = 180;
                break;
            case Right:
            default:
                orientation = 0;
                break;
        }
        for (int i = 0; i < smoothPath.size() - 1; i++) {
            Vector2 posDiff = smoothPath.get(i + 1).fnAdd(smoothPath.get(i).fnMultiply(-1));
            double distance = Math.sqrt(Math.pow(posDiff.i(), 2) + Math.pow(posDiff.j(), 2));
            String distanceStr = "f" + distance + _TRAILER;
            double rotation;
            String rotateDirection;
            double alpha = Math.atan(((double) posDiff.i()) / ((double) posDiff.j()));
            double angle;
            if (posDiff.i() > 0) {
                rotateDirection = "r";
                if (posDiff.j() > 0) {
                    angle = alpha + 270;
                } else {
                    angle = alpha + 180;
                }
            } else {
                rotateDirection = "l";
                if (posDiff.j() > 0) {
                    angle = alpha + 0;
                } else {
                    angle = alpha + 90;
                }
            }
            rotation = angle - orientation;
            String rotationStr = rotateDirection + rotation + _TRAILER;
            result += rotationStr + distanceStr;
        }
        return result;
    }

    private String _compileMap(Map map, int[][] explored) {
        String strResult = Descriptor.stringify(map, explored);
        String[] hexDesc = Descriptor.toHex(strResult);
        return hexDesc[0] + _DESC_SEPARATOR + hexDesc[1];
    }

    private String _compileCalibration(CalibrationType calType) {
        switch (calType) {
            case Right:
                return _CAL_RIGHT;
            case Front_LR:
                return _CAL_FRONT_LR;
            case Front_ML:
                return _CAL_FRONT_ML;
            case Front_MR:
                return _CAL_FRONT_MR;
            default:
                return "";
        }
    }

    @Override
    public void sendCalibrationCommand(CalibrationType calType) {
        try {
            String message = _TO_ARDUINO_MARKER + _compileCalibration(calType) + _TRAILER;
            _socketCommunicator.echo(message);
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Arduino
    @Override
    public void sendMoveCommand(List<RobotAction> actions) {
        try {
            String message = _TO_ARDUINO_MARKER + _compileActions(actions);
            _socketCommunicator.echo(message);
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendSmoothMoveCommand(Map map, List<Vector2> path) {
        try {
            String message = _TO_ARDUINO_MARKER + _compileActions(map, path);
            _socketCommunicator.echo(message);
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendSensingRequest() {
        try {
            String message = _TO_ARDUINO_MARKER + _SENSING_REQUEST + _TRAILER;
            _socketCommunicator.echo(message);
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Android
    @Override
    public void sendInfoToAndroid(Map map, int[][] explored, LinkedList<RobotAction> actions) {
        try {
            String message
                    = _TO_ANDROID_MARKER + _compileMap(map, explored)
                    + _MSG_SEPARATOR
                    + _compileActions(actions);
            _socketCommunicator.echo(message);
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String _readAsString() {
        try {
            return _socketCommunicator.read();
        } catch (IOException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERRR";
    }

    @Override
    public void listen(Runnable handler) {
        new Thread() {
            @Override
            public void run() {
                Timer probingTimer = new Timer();
                probingTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("listening...");
                        String readResult = _readAsString();
                        _inputBuffer = "";
                        if (!readResult.isEmpty()) {
                            _inputBuffer = readResult;
                            handler.run();
                            System.out.println();
//                                probingTimer.cancel();
                        }
                    }
                }, 0, _PROBING_PERIOD);
            }
        }.start();
    }
}
