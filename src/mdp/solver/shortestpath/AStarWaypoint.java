package mdp.solver.shortestpath;

import mdp.Direction;
import mdp.Waypoint;

public class AStarWaypoint extends Waypoint {
    
    private final int _hval;
    private int _gval;
    private Direction _parentDir;
    
    public AStarWaypoint(Waypoint wp, int hval, int gval, Direction parentIndex) {
        super(wp.position(), wp.specialState(), wp.obstacleState());
        _hval = hval;
        _gval = gval;
        _parentDir = parentIndex;
    }
    public AStarWaypoint(Waypoint wp) {
        this(wp, 1000000, 1000000, Direction.Up);
    }
    public AStarWaypoint() {
        this(new Waypoint());
    }
    

    public int hval() { return _hval; }
    public int gval() { return _gval; }
    public void gval(int gval) { _gval = gval; }

    public Direction parentDir() { return _parentDir; }

    public void parentDir(Direction parentDir) { _parentDir = parentDir; }
    
    
    public int fval() { return _hval + _gval; }
}
