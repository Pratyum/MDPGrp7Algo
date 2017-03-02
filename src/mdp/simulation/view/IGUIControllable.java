package mdp.simulation.view;

import mdp.map.Map;
import mdp.robot.Robot;

public interface IGUIControllable extends IGUIUpdatable {

    MainFrame getMainFrame();

    Map getMap();

    Robot getRobot();

    void reset();
    
}
