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
import mdp.common.Vector2;
import mdp.map.Descriptor;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.map.WPSpecialState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.solver.exploration.ExplorationSolver;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class EventHandler {
    
    private IGUIControllable _gui;
    private Timer _robotAnimation;

    public EventHandler(IGUIControllable gui) {
        _gui = gui;
        _gui.reset();
        
        // frame event
        _gui.getMainFrame().addWindowListener(_onClose());
        
        // obstacle event
        _gui.getMainFrame()
            .getMainPanel()
            .getGridPanel()
            .getGridContainer().setGridAdapter(_onToggleObstacle());
        
        // descriptor control event
        _gui.getMainFrame()
            .getMainPanel()
            .getDescCtrlPanel()
            .getOpenDescBtn().addMouseListener(_onOpenDesc());
        _gui.getMainFrame()
            .getMainPanel()
            .getDescCtrlPanel()
            .getSaveDescBtn().addMouseListener(_onSaveDesc());
        
        // run control event
        _gui.getMainFrame()
            .getMainPanel()
            .getRunCtrlPanel()
            .getExplorationBtn().addMouseListener(_onExploration());
        _gui.getMainFrame()
            .getMainPanel()
            .getRunCtrlPanel()
            .getShortestPathBtn().addMouseListener(_onShortestPath());
        _gui.getMainFrame()
            .getMainPanel()
            .getRunCtrlPanel()
            .getCombinedBtn().addMouseListener(_onCombined());
        
        // run control event
        _gui.getMainFrame()
            .getMainPanel()
            .getIntrCtrlPanel()
            .getResetBtn().addMouseListener(_onReset());
        _gui.getMainFrame()
            .getMainPanel()
            .getIntrCtrlPanel()
            .getStopBtn().addMouseListener(_onStop());
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
                    String filePath = _gui.getMainFrame()
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
                    Descriptor.saveToFile(filePath, _gui.getMap(), explored);
                } catch (IOException ex) {
                    Logger.getLogger(IGUIControllable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    private MouseAdapter _onOpenDesc() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    _gui.reset();
                    String filePath = _gui.getMainFrame()
                        .getMainPanel()
                        .getDescCtrlPanel()
                        .getFilePathBtn().getText();
                    _gui.update(Descriptor.parseFromFile(filePath));
                } catch (IOException ex) {
                    Logger.getLogger(IGUIControllable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    private MouseAdapter _onExploration() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ExplorationSolver.main(_gui.getMap());
                } catch (InterruptedException ex) {
                    Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    private MouseAdapter _onShortestPath() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AStarSolver solver = new AStarSolver();
                AStarSolverResult solveResult = solver.solve(_gui.getMap(), _gui.getRobot());
                _gui.getMap().highlight(solveResult.openedPoints, WPSpecialState.IsOpenedPoint);
                _gui.getMap().highlight(solveResult.closedPoints, WPSpecialState.IsClosedPoint);
                _gui.getMap().highlight(solveResult.shortestPath, WPSpecialState.IsPathPoint);
                LinkedList<RobotAction> actions = RobotAction
                                .fromPath(_gui.getRobot(), solveResult.shortestPath);

                _robotAnimation = new Timer();
                _robotAnimation.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!actions.isEmpty()) {
                            _gui.getRobot().execute(actions.pop());
                            _gui.update(_gui.getMap(), _gui.getRobot());
                        } else {
                            this.cancel();
                        }
                    }
                }, 200, 200);
            }
        };
    }
    private MouseAdapter _onCombined() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // combined code here
            }
        };
    }
    private MouseAdapter _onStop() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _robotAnimation.cancel();
                _gui.getMap().clearAllHighlight();
                _gui.update(_gui.getMap(), new Robot());
            }
        };
    }
    private MouseAdapter _onReset() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _robotAnimation.cancel();
                _gui.reset();
            }
        };
    }
    private MouseAdapter _onToggleObstacle() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GridSquare target = (GridSquare) e.getSource();
                Vector2 clickedPos = target.position();
                WPObstacleState obsState = _gui.getMap().getPoint(clickedPos).obstacleState();
                if (obsState.equals(WPObstacleState.IsActualObstacle)) {
                    _gui.getMap().clearObstacle(clickedPos);
                } else {
                    _gui.getMap().addObstacle(clickedPos);
                }
                System.out.println("Toggled obstacle at " + clickedPos);
                target.toggleBackground();
            }
        };
    }
    
}
