package mdp.communication;

import java.io.IOException;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import mdp.map.Descriptor;
import mdp.map.Map;
import mdp.robot.RobotAction;

public class Translator implements ITranslatable {

    private static final String _TO_ARDUINO_MARKER = "a";
    private static final String _TO_ANDROID_MARKER = "b";

    private static final String _MOVE_FORWARD_STR = "f";
    private static final String _MOVE_BACKWARD_STR = "b";
    private static final String _ROTATE_LEFT_STR = "l";
    private static final String _ROTATE_RIGHT_STR = "r";

    private static final String _SENSING_REQUEST = "s";
    private static final String _TRAILER = "|";

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
//            System.out.println("result = " + result);
//            System.out.println("nextActionStr = " + nextActionStr);
            if (result.length() != 0) {
                String lastAction = Character.toString(result.charAt(result.length() - 1));
//                System.out.println("lastAction = " + lastAction);
                if (lastAction.equals(nextActionStr)) {
//                    System.out.println("Counting up");
                    count++;
                } else {
                    boolean noCount = lastAction.equals(_ROTATE_LEFT_STR) || lastAction.equals(_ROTATE_RIGHT_STR);
                    result += (noCount ? "" : count) + _TRAILER + nextActionStr;
                    count = 1;
                }
            } else {
                result += nextActionStr;
            }
//            System.out.println();
        }
        result += count + _TRAILER;
        System.out.println("Sending out: " + result);
        return result;
    }

    private String _compileMap(Map map, boolean[][] explored) {
        String[] hexDesc = Descriptor.toHex(Descriptor.stringify(map, explored));
        return hexDesc[0] + hexDesc[1];
    }

    // Arduino
    @Override
    public void sendShortestPath(List<RobotAction> actions) throws IOException {
        String message = _TO_ARDUINO_MARKER + _compileActions(actions);
        _socketCommunicator.echo(message);
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
    public void sendMapInfo(Map map, boolean[][] explored) throws IOException {
        String message = _TO_ANDROID_MARKER + _compileMap(map, explored) + _TRAILER;
        _socketCommunicator.echo(message);
    }

    private String _readAsString() throws IOException {
        return _socketCommunicator.read();
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
                        try {
                            String readResult = _readAsString();
                            _inputBuffer = "";
                            if (!readResult.isEmpty()) {
                                _inputBuffer = readResult;
                                handler.run();
                                System.out.println();
//                                probingTimer.cancel();
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(ITranslatable.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, 0, _PROBING_PERIOD);
            }
        }.start();
    }
}
