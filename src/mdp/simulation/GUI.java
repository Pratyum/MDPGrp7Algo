package mdp.simulation;

import mdp.map.Map;
import mdp.robot.Robot;

public class GUI implements IGUIControllable {
    
    private Map _map;
    private Robot _robot;
    
    private MainFrame _mainFrame;
    private EventHandler _eventHandler;

    public GUI() {
        _mainFrame = new MainFrame();
        _eventHandler = new EventHandler(this);
    }

    @Override
    public Map getMap() {
        return _map;
    }

    @Override
    public Robot getRobot() {
        return _robot;
    }

    @Override
    public MainFrame getMainFrame() {
        return _mainFrame;
    }
    
    @Override
    public void reset() {
        this.update(new Map(), new Robot());
    }
    
    @Override
    public void update(Map map, Robot robot) {
        _map = map;
        _robot = robot;
        _mainFrame
            .getMainPanel()
            .getGridPanel()
            .getGridContainer().fillGrid(_map, _robot);
        _mainFrame.revalidate();
    }
    @Override
    public void update(Map map) {
        update(map, _robot);
    }
    @Override
    public void update(Robot robot) {
        update(_map, robot);
    }
    
    @Override
    public void trigger(ManualTrigger trigger) {
        RunControlPanel runCtrlPanel = _mainFrame.getMainPanel().getRunCtrlPanel();
        switch (trigger) {
            case Exploration:
                runCtrlPanel.getExplorationBtn().doClick();
                break;
            case ShortestPath:
                runCtrlPanel.getShortestPathBtn().doClick();
                break;
            case Combined:
                runCtrlPanel.getCombinedBtn().doClick();
                break;
        }
    }
    
}