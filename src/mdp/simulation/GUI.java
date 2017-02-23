package mdp.simulation;

import mdp.Map;
import mdp.Robot;

public class GUI {
    
    private Map _map;
    private Robot _robot;
    
    private MainFrame _mainFrame;

    public GUI() {
        _mainFrame = new MainFrame();
    }
    
    public void update(Map map, Robot robot) {
        _map = map;
        _robot = robot;
        _mainFrame
            .getMainPanel()
            .getGridPanel()
            .getGridContainer().fillGrid(_map, _robot);
        _mainFrame.revalidate();
    }
    
}