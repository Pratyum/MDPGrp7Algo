package mdp.simulation;

import mdp.map.Map;
import mdp.robot.Robot;

public interface IGUIUpdatable {
    
    public enum ManualTrigger { Exploration, ShortestPath, Combined, Stop }

    void trigger(ManualTrigger trigger);

    void update(Map map, Robot robot);

    void update(Map map);

    void update(Robot robot);
    
}
