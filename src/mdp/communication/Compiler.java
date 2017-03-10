package mdp.communication;

import java.util.ArrayList;
import java.util.List;
import mdp.common.Vector2;
import mdp.map.Descriptor;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.solver.exploration.CalibrationType;

public class Compiler {

    private static final String _MOVE_FORWARD = "f";
    private static final String _MOVE_BACKWARD = "b";
    private static final String _ROTATE_LEFT = "l";
    private static final String _ROTATE_RIGHT = "r";

    private static final String _CAL_FRONT_LR = "x";
    private static final String _CAL_FRONT_ML = "y";
    private static final String _CAL_FRONT_MR = "z";
    private static final String _CAL_RIGHT = "c";

    private static final String _SENSING_REQUEST = "s";
    private static final String _TRAILER = "|";

    private static final String DESC_SEPARATOR = "g";

    static String compileActions(List<RobotAction> actions) {
        String result = "";
        int count = 1;
        String lastAction = "";
        for (RobotAction action : actions) {
            String nextActionStr;
            switch (action) {
                case MoveForward:
                    nextActionStr = _MOVE_FORWARD;
                    break;
                case MoveBackward:
                    nextActionStr = _MOVE_BACKWARD;
                    break;
                case RotateLeft:
                    nextActionStr = _ROTATE_LEFT;
                    break;
                case RotateRight:
                    nextActionStr = _ROTATE_RIGHT;
                    break;
                default:
                    nextActionStr = " ";
                    break;
            }
            if (result.length() != 0) {
                lastAction = Character.toString(result.charAt(result.length() - 1));
                if (lastAction.equals(nextActionStr)) {
                    boolean isRotating = lastAction.equals(_ROTATE_LEFT) || lastAction.equals(_ROTATE_RIGHT);
                    if (isRotating) {
                        result += _TRAILER + lastAction;
                    } else {
                        count++;
                    }
                } else {
                    boolean isRotating = lastAction.equals(_ROTATE_LEFT) || lastAction.equals(_ROTATE_RIGHT);
                    result += (isRotating ? "" : count) + _TRAILER + nextActionStr;
                    count = 1;
                }
            } else {
                result += nextActionStr;
            }
        }
        boolean isRotating;
        if (result.length() != 1) {
            isRotating = lastAction.equals(_ROTATE_LEFT) || lastAction.equals(_ROTATE_RIGHT);
        } else {
            isRotating = result.equals(_ROTATE_LEFT) || result.equals(_ROTATE_RIGHT);
        }

        result += (isRotating ? "" : count) + _TRAILER;
        System.out.println("Sending out: " + result);
        return result;
    }

    static String compileActions(Map map, List<Vector2> path) {
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
            String rotationStr = rotateDirection + String.format("%.3f", rotation) + _TRAILER;
            result += rotationStr + distanceStr;
        }
        return result;
    }

    static String compileMap(Map map, int[][] explored) {
        String strResult = Descriptor.stringify(map, explored);
        String[] hexDesc = Descriptor.toHex(strResult);
        return hexDesc[0] + DESC_SEPARATOR + hexDesc[1];
    }

    static String compileCalibration(CalibrationType calType) {
        switch (calType) {
            case Right:
                return _CAL_RIGHT + _TRAILER;
            case Front_LR:
                return _CAL_FRONT_LR + _TRAILER;
            case Front_ML:
                return _CAL_FRONT_ML + _TRAILER;
            case Front_MR:
                return _CAL_FRONT_MR + _TRAILER;
            default:
                return "";
        }
    }

    static String compileSensingRequest() {
        return _SENSING_REQUEST + _TRAILER;
    }
}
