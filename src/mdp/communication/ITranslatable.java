package mdp.communication;

import java.io.IOException;
import java.util.List;
import mdp.map.Map;
import mdp.robot.RobotAction;

public interface ITranslatable {

    String getInputBuffer();
    
    void connect(Runnable callback);

    void listen(Runnable handler);

    void sendMapInfo(Map map, boolean[][] explored) throws IOException;
    
    void sendSensingRequest();

    void sendShortestPath(List<RobotAction> actions) throws IOException;
    
}
