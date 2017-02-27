package mdp.communication;

import java.io.IOException;
import java.util.List;
import mdp.map.Map;
import mdp.robot.RobotAction;

public interface ITranslatable {

    String getInputBuffer();

    void listen(Runnable handler);

    void sendToAndroid(Map map, boolean[][] explored) throws IOException;

    void sendToArduino(List<RobotAction> actions) throws IOException;
    
}
