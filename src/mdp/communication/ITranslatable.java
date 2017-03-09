package mdp.communication;

import java.io.IOException;
import java.util.List;
import mdp.common.Vector2;
import mdp.map.Map;
import mdp.robot.RobotAction;

public interface ITranslatable {

    String getInputBuffer();
    
    void connect(Runnable callback);

    void listen(Runnable handler);

    void sendMapInfo(Map map, int[][] explored);
    
    void sendSensingRequest();

    void sendMoveCommand(List<RobotAction> actions);
    
    void sendSmoothMoveCommand(Map map, List<Vector2> path);
    
}
