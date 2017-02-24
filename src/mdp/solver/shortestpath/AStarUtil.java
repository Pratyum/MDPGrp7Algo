package mdp.solver.shortestpath;

import mdp.common.Direction;
import mdp.robot.Robot;
import mdp.common.Vector2;

public class AStarUtil {
    
    public static int getMDistance(Vector2 point1, Vector2 point2) {
        return Math.abs(point1.i() - point2.i()) + 
                Math.abs(point1.j() - point2.j());
    }
    
    public static int getMoveCost(Robot robot, Direction direction) {
        if (direction == robot.orientation()) {
            return 1;
        } else if (direction == robot.orientation().getBehind()) {
            return 3;
        } else {
            return 2;
        }
    }
    
}
