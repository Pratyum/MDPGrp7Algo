package mdp.solver.shortestpath;

import java.util.ArrayList;
import java.util.List;
import mdp.Vector2;

public class AStarSolverResult {
    public List<Vector2> shortestPath;
    public List<Vector2> openedPoints;
    public List<Vector2> closedPoints;
    
    AStarSolverResult() {
        shortestPath = new ArrayList<Vector2>();
        openedPoints = new ArrayList<Vector2>();
        closedPoints = new ArrayList<Vector2>();
    }
}
