package mdp.solver;

import mdp.Vector2;
import mdp.Robot;
import mdp.Map;
import java.util.List;
import mdp.Map;
import mdp.Robot;
import mdp.Vector2;

public interface Solver {
    List<Vector2> solve(Map map, Robot robot);
}
