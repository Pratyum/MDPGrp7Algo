package mdp.simulation;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.common.Vector2;
import mdp.map.Descriptor;
import mdp.map.WPObstacleState;
import mdp.map.WPSpecialState;
import mdp.solver.exploration.ExplorationSolver;
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
        
        gridContainer.setGridAdapter(_onToggleObstacle());
        _mainFrame
            .getMainPanel()
            .getDescCtrlPanel()
            .getOpenDescBtn().addMouseListener(_onOpenDesc());
        _mainFrame
            .getMainPanel()
            .getDescCtrlPanel()
            .getSaveDescBtn().addMouseListener(_onSaveDesc());
        _mainFrame
            .getMainPanel()
            .getRunCtrlPanel()
            .getExplorationBtn().addMouseListener(_onExploration());
        _mainFrame
            .getMainPanel()
            .getRunCtrlPanel()
            .getShortestPathBtn().addMouseListener(_onShortestPath());
        _mainFrame
            .getMainPanel()
            .getIntrCtrlPanel()
            .getResetBtn().addMouseListener(_onReset());
        _mainFrame
            .getMainPanel()
            .getIntrCtrlPanel()
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
    
    private MouseAdapter _onSaveDesc() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String filePath = _mainFrame
                        .getMainPanel()
                        .getDescCtrlPanel()
                        .getFilePathBtn().getText();
                    /////// for testing
                    boolean[][] explored = new boolean[Map.DIM_I][Map.DIM_J];
                    for (int i = 0; i < Map.DIM_I; i++) {
                        for (int j = 0; j < Map.DIM_J; j++) {
                            explored[i][j] = true;
                        }
                    }
                    ///////
                    Descriptor.saveToFile(filePath, _map, explored);
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    private MouseAdapter _onOpenDesc() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    _reset();
                    String filePath = _mainFrame
                        .getMainPanel()
                        .getDescCtrlPanel()
                        .getFilePathBtn().getText();
                    update(Descriptor.parseFromFile(filePath));
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    private MouseAdapter _onExploration() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ExplorationSolver.main(new String[0]);
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
    public void update(Map map) {
        update(map, _robot);
    }
    
}