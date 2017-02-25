package mdp.communication;

import java.io.IOException;
import java.util.List;
import mdp.map.Descriptor;
import mdp.map.Map;
import mdp.robot.RobotAction;

public class Translator {
    
    private SocketCommunicator _socketCommunicator;
    
    private static final String _TO_ARDUINO_MARKER = "a";
    private static final String _TO_ANDROID_MARKER = "b";
    
    private static final String _MOVE_FORWARD_STR = "f";
    private static final String _MOVE_BACKWARD_STR = "b";
    private static final String _ROTATE_LEFT_STR = "l";
    private static final String _ROTATE_RIGHT_STR = "r";
    
    public Translator() throws IOException {
        _socketCommunicator = new SocketCommunicator();
    }
    
    private String _compileActions(List<RobotAction> actions) {
        String result = "";
        for (RobotAction action : actions) {
            switch (action) {
                case MoveForward:
                    result += _MOVE_FORWARD_STR;
                    break;
                case MoveBackward:
                    result += _MOVE_BACKWARD_STR;
                    break;
                case RotateLeft:
                    result += _ROTATE_LEFT_STR;
                    break;
                case RotateRight:
                    result += _ROTATE_RIGHT_STR;
                    break;
                default:
                    result += " ";
                    break;
                    
            }
        }
        return result;
    }
    
    private String _compileMap(Map map, boolean[][] explored) {
        String[] hexDesc = Descriptor.toHex(Descriptor.stringify(map, explored));
        return hexDesc[0] + "-" + hexDesc[1];
    }
    
    public void sendToArduino(List<RobotAction> actions) throws IOException {
        String message = _TO_ARDUINO_MARKER + _compileActions(actions);
        _socketCommunicator.echo(message);
    }
    
    public void sendToAndroid(Map map, boolean[][] explored) throws IOException {
        String message = _TO_ANDROID_MARKER + _compileMap(map, explored);
        _socketCommunicator.echo(message);
    }
    
    public String readFromArduino() throws IOException {
        return _socketCommunicator.read();
    }
}
