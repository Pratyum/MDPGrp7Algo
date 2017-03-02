package mdp.simulation.event;

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
import mdp.Main;
import mdp.common.Vector2;
import mdp.map.Descriptor;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.map.WPSpecialState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.simulation.view.GridSquare;
import mdp.simulation.view.HexFrame;
import mdp.simulation.view.IGUIControllable;
import mdp.solver.exploration.ExplorationSolver;
import mdp.solver.exploration.Terminator;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class EventHandler implements IHandleable {
    
    private IGUIControllable _gui;
    private Timer _shortestPathThread;
    private Thread _explorationThread;
    private static boolean _isShortestPath = true;

    public static boolean isShortestPath() {
        return _isShortestPath;
    }

    public EventHandler(IGUIControllable gui) {
        _gui = gui;
        _gui.reset();

        // frame event
        _gui.getMainFrame().addWindowListener(_wrapWindowAdapter(GUIWindowEvent.OnClose));

        // obstacle event
        _gui.getMainFrame()
                .getMainPanel()
                .getGridPanel()
                .getGridContainer().setGridAdapter(_wrapMouseAdapter(GUIClickEvent.OnToggleObstacle));

        // descriptor control event
        _gui.getMainFrame()
                .getMainPanel()
                .getDescCtrlPanel()
                .getOpenDescBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnOpen));
        _gui.getMainFrame()
                .getMainPanel()
                .getDescCtrlPanel()
                .getSaveDescBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnSave));
        _gui.getMainFrame()
                .getMainPanel()
                .getDescCtrlPanel()
                .getGetHexBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnGetHex));

        // run control event
        _gui.getMainFrame()
                .getMainPanel()
                .getRunCtrlPanel()
                .getExplorationBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnExploration));
        _gui.getMainFrame()
                .getMainPanel()
                .getRunCtrlPanel()
                .getShortestPathBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnShortestPath));
        _gui.getMainFrame()
                .getMainPanel()
                .getRunCtrlPanel()
                .getCombinedBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnCombined));

        // run control event
//        _gui.getMainFrame()
//                .getMainPanel()
//                .getIntrCtrlPanel()
//                .getResetBtn().addMouseListener(_onReset());
//        _gui.getMainFrame()
//                .getMainPanel()
//                .getIntrCtrlPanel()
//                .getStopBtn().addMouseListener(_onStop());
        _gui.getMainFrame()
                .getMainPanel()
                .getIntrCtrlPanel()
                .getRestartBtn().addMouseListener(_wrapMouseAdapter(GUIClickEvent.OnRestart));
    }
        
    @Override
    public void resolveBtnHandler(GUIClickEvent hdlr, MouseEvent e) {
        switch (hdlr) {
            case OnToggleObstacle: _onToggleObstacle(e); break;
            case OnOpen: _onOpenDesc(e); break;
            case OnSave: _onSaveDesc(e); break;
            case OnGetHex: _onGetHex(e); break;
            case OnExploration: _onExploration(e); break;
            case OnShortestPath: _onShortestPath(e); break;
            case OnCombined: _onCombined(e); break;
            case OnRestart: _onRestart(e); break;
        }
    }
    @Override
    public void resolveFrameHandler(GUIWindowEvent hdlr, WindowEvent e) {
        switch (hdlr) {
            case OnClose: _onClose(e); break;
        }
    }
    
    private MouseAdapter _wrapMouseAdapter(GUIClickEvent hdlr) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resolveBtnHandler(hdlr, e);
            }
        };
        
    }
    private WindowAdapter _wrapWindowAdapter(GUIWindowEvent hdlr) {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resolveFrameHandler(hdlr, e);
            }
        };
    }

    private void _onClose(WindowEvent e) {
        System.exit(0);
    }

    private void _onSaveDesc(MouseEvent e) {
        try {
            String filePath = _gui.getMainFrame()
                    .getMainPanel()
                    .getDescCtrlPanel()
                    .getFilePathTextField().getText();
            /////// for testing
            boolean[][] explored = new boolean[Map.DIM_I][Map.DIM_J];
            for (int i = 0; i < Map.DIM_I; i++) {
                for (int j = 0; j < Map.DIM_J; j++) {
                    explored[i][j] = true;
                }
            }
            ///////
            Descriptor.saveToFile(filePath, _gui.getMap(), explored);
            System.out.println("Save desc completed.");
        } catch (IOException ex) {
            Logger.getLogger(IGUIControllable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void _onOpenDesc(MouseEvent e) {
        try {
            _gui.reset();
            String filePath = _gui.getMainFrame()
                    .getMainPanel()
                    .getDescCtrlPanel()
                    .getFilePathTextField().getText();
            System.out.println(filePath);
            _gui.update(Descriptor.parseFromFile(filePath));
            System.out.println("Open desc completed.");
        } catch (IOException ex) {
            Logger.getLogger(IGUIControllable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void _onGetHex(MouseEvent e) {
        Map map = ExplorationSolver.getMapViewer().getSubjectiveMap();
        int[][] explored = ExplorationSolver.getMapViewer().getExplored();

        String descStr = Descriptor.stringify(map, explored);
        String content = String.join("\n", Descriptor.toHex(descStr));

        System.out.println(map.toString(new Robot()));

        HexFrame hexFrame = new HexFrame();
        hexFrame
                .getHexFramePanel()
                .getHexTextArea().setText(content);

        System.out.println("Get hex completed.");
    }

    private void _onExploration(MouseEvent e) {
        int exePeriod = Integer.parseInt(
                _gui.getMainFrame()
                        .getMainPanel()
                        .getRunCtrlPanel()
                        .getExePeriod().getText()
        );
        _explorationThread = new Thread(() -> {
            try {
                _explorationProcedure(exePeriod, () -> {
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        _explorationThread.start();
    }

    private void _onShortestPath(MouseEvent e) {
        int exePeriod = Integer.parseInt(
                _gui.getMainFrame()
                        .getMainPanel()
                        .getRunCtrlPanel()
                        .getExePeriod().getText()
        );
        _shortestPathProcedure(exePeriod);
    }

    private void _onCombined(MouseEvent e) {
        int exePeriod = Integer.parseInt(
                _gui.getMainFrame()
                        .getMainPanel()
                        .getRunCtrlPanel()
                        .getExePeriod().getText()
        );
        _explorationThread = new Thread(() -> {
            try {
                // exploration
                _explorationProcedure(exePeriod, () -> {
                    System.out.println("Is at COMBINED callback");
                    // shortest path
                    _shortestPathProcedure(exePeriod);
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        _explorationThread.start();
    }

//    private MouseAdapter _onStop() {
//        return new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (_shortestPathThread != null) {
//                    _shortestPathThread.cancel();
//                }
//                if (_explorationThread != null) {
//                    _explorationThread.stop();
//                }
//                _gui.getMap().clearAllHighlight();
//                _gui.update(_gui.getMap(), new Robot());
//
//                System.out.println("Stop completed.");
//            }
//        };
//    }
//
//    private MouseAdapter _onReset() {
//        return new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (_shortestPathThread != null) {
//                    _shortestPathThread.cancel();
//                }
//                if (_explorationThread != null) {
//                    _explorationThread.stop();
//                }
//                _gui.reset();
//
//                System.out.println("Reset completed.");
//            }
//        };
//    }

    private void _onRestart(MouseEvent e) {
        _gui.getMainFrame().dispose();
        Main.restartGUI();
        ExplorationSolver.restart();
        _isShortestPath = true;
        System.out.println("Restart completed.");
    }

    private void _onToggleObstacle(MouseEvent e) {
        GridSquare target = (GridSquare) e.getSource();
        Vector2 clickedPos = target.position();
        WPObstacleState obsState = _gui.getMap().getPoint(clickedPos).obstacleState();
        if (obsState.equals(WPObstacleState.IsActualObstacle)) {
            _gui.getMap().clearObstacle(clickedPos);
        } else {
            _gui.getMap().addObstacle(clickedPos);
        }
        target.toggleBackground();

        System.out.println("Toggled obstacle at " + clickedPos);
    }

    // shared procedures
    private void _explorationProcedure(int exePeriod, Runnable callback) throws InterruptedException {
        System.out.println("Starting Exploration");
        _isShortestPath = false;
        int termCoverage = Integer.parseInt(_gui
                .getMainFrame()
                .getMainPanel()
                .getIntrCtrlPanel()
                .getTermCoverageText().getText()
        );
        int termTime = Integer.parseInt(_gui
                .getMainFrame()
                .getMainPanel()
                .getIntrCtrlPanel()
                .getTermTimeText().getText()
        );
        
        Runnable interruptCallback = () -> {
            System.out.println(">> STOP <<");
            Robot curRobot = ExplorationSolver.getRobot();
            int[][] explored = ExplorationSolver.getMapViewer().getExplored();
            _explorationThread.stop();
            Map finalMap = new Map(explored);
            ExplorationSolver.goBackToStart(finalMap, curRobot, callback);
            Main.getGUI().update(finalMap);
        };
        Runnable nonInterruptCallback = () -> {
            Robot curRobot = ExplorationSolver.getRobot();
            int[][] explored = ExplorationSolver.getMapViewer().getExplored();
            Map finalMap = new Map(explored);
            ExplorationSolver.goBackToStart(finalMap, curRobot, callback);
            Main.getGUI().update(finalMap);
        };
        if (termCoverage != 0) {
            new Terminator(termCoverage / 100f, interruptCallback).observe();
        } else if (termTime != 0) {
            new Terminator(termTime, interruptCallback).observe();
        }
        ExplorationSolver.solve(_gui.getMap(), exePeriod);
        System.out.println("Exploration completed.");
        nonInterruptCallback.run();
    }

    private void _shortestPathProcedure(int exePeriod) {
        System.out.println("Starting Shortest Path");
        _isShortestPath = true;
        AStarSolver solver = new AStarSolver();
        AStarSolverResult solveResult = solver.solve(_gui.getMap(), _gui.getRobot());
        _gui.getMap().highlight(solveResult.openedPoints, WPSpecialState.IsOpenedPoint);
        _gui.getMap().highlight(solveResult.closedPoints, WPSpecialState.IsClosedPoint);
        _gui.getMap().highlight(solveResult.shortestPath, WPSpecialState.IsPathPoint);
        LinkedList<RobotAction> actions = RobotAction
                .fromPath(_gui.getRobot(), solveResult.shortestPath);
        
        _shortestPathThread = new Timer();
        _shortestPathThread.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!actions.isEmpty()) {
                    _gui.getRobot().execute(actions.pop());
                    _gui.update(_gui.getMap(), _gui.getRobot());
                } else {
                    System.out.println("Shortest path completed.");
                    this.cancel();
                }
            }
        }, exePeriod, exePeriod);
    }
}
