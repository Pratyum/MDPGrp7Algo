package mdp.solver.exploration;

import java.util.Timer;
import java.util.TimerTask;
import mdp.Main;
import mdp.map.Map;
import mdp.simulation.IGUIUpdatable;

public class Terminator {
    
    private enum TerminatorType { Coverage, Time }
    
    private float _maxCoverage;
    private long _maxDiffTime;
    private TerminatorType _terminationType;
    
    private Timer _thread;
    
    public Terminator(float maxCoverage) {
        _maxCoverage = maxCoverage;
        _terminationType = TerminatorType.Coverage;
    }
    
    public Terminator(long maxDiffTime) {
        _maxDiffTime = maxDiffTime;
        _terminationType = TerminatorType.Time;
    }
    
    public void startRun() {
        switch (_terminationType) {
            case Coverage:
                int maxExplored = Map.DIM_I * Map.DIM_J;
                _thread = new Timer();
                _thread.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int[][] explored = ExplorationSolver.getMapViewer().getExplored();
                        // do stuff
                    }
                }, 0, 50);
                break;
            case Time:
                _thread = new Timer();
                _thread.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Main.getGUI().trigger(IGUIUpdatable.ManualTrigger.Stop);
                    }
                }, _maxDiffTime);
                break;
        }
    }
    
}
