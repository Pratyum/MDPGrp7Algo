package mdp.simulation;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import mdp.Map;
import mdp.Robot;
import mdp.RobotAction;
import mdp.Vector2;
import mdp.WPObstacleState;
import mdp.WPSpecialState;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class GUI {
    
    private Map _map;
    private Robot _robot;
    
    private MainFrame _mainFrame;
    private Timer _robotAnimation;

    public GUI() {
        _mainFrame = new MainFrame();
        _reset();
        _mainFrame.addWindowListener(_onClose());
        
        GridContainer gridContainer = _mainFrame
            .getMainPanel()
            .getGridPanel()
            .getGridContainer();                
        ControlPanel controlPanel = _mainFrame
            .getMainPanel()
            .getControlPanel();
        
        gridContainer.setGridAdapter(_onToggleObstacle());
        controlPanel
            .getShortestPathBtn().addMouseListener(_onShortestPath());
        controlPanel
            .getResetBtn().addMouseListener(_onReset());
        controlPanel
            .getStopBtn().addMouseListener(_onStop());
    }
    
    private void _reset() {
        this.update(new Map(), new Robot());
    }
    
    private WindowAdapter _onClose() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
    }
    
    private MouseAdapter _onShortestPath() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AStarSolver solver = new AStarSolver();
                AStarSolverResult solveResult = solver.solve(_map, _robot);
                _map.highlight(solveResult.openedPoints, WPSpecialState.IsOpenedPoint);
                _map.highlight(solveResult.closedPoints, WPSpecialState.IsClosedPoint);
                _map.highlight(solveResult.shortestPath, WPSpecialState.IsPathPoint);
                LinkedList<RobotAction> actions = RobotAction
                                .fromPath(_robot, solveResult.shortestPath);

                _robotAnimation = new Timer();
                _robotAnimation.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!actions.isEmpty()) {
                            _robot.execute(actions.pop());
                            update(_map, _robot);
                        } else {
                            this.cancel();
                        }
                    }
                }, 200, 200);
            }
        };
    }
    private MouseAdapter _onStop() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _robotAnimation.cancel();
                _map.clearAllHighlight();
                update(_map, new Robot());
            }
        };
    }
    private MouseAdapter _onReset() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _robotAnimation.cancel();
                _reset();
            }
        };
    }
    private MouseAdapter _onToggleObstacle() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GridSquare target = (GridSquare) e.getSource();
                Vector2 clickedPos = target.position();
                WPObstacleState obsState = _map.getPoint(clickedPos).obstacleState();
                if (obsState.equals(WPObstacleState.IsActualObstacle)) {
                    _map.clearObstacle(clickedPos);
                } else {
                    _map.addObstacle(clickedPos);
                }
                System.out.println("Toggled obstacle at " + clickedPos);
                target.toggleBackground();
            }
        };
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