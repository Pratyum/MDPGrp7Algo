package mdp.solver.shortestpath;

import java.util.ArrayList;
import java.util.List;
import mdp.common.Direction;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.map.Map;
import mdp.map.WPObstacleState;

public class AStarUtil {

    public static int getMDistance(Vector2 point1, Vector2 point2) {
        return Math.abs(point1.i() - point2.i())
                + Math.abs(point1.j() - point2.j());
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
    
    public static int getSmoothMoveCost(Robot robot, Direction direction) {
        if (direction == robot.orientation()) {
            return 3;
        } else if (direction == robot.orientation().getBehind()) {
            return 2;
        } else {
            return 2;
        }
    }

    public static List<Vector2> smoothenPath(Map map, List<Vector2> path, boolean fromEnd) {
        List<Vector2> result = new ArrayList<>();
        Vector2 lastSavedPos = new Robot().position();
        Vector2 lastSavedTurnPos = null;
        Vector2 prevPos = null;
        result.add(lastSavedPos);
        for (Vector2 curPos : path) {// check existence of obstacle in rectangular area

            int minI = Integer.min(lastSavedPos.i(), curPos.i());
            int maxI = Integer.max(lastSavedPos.i(), curPos.i());
            int minJ = Integer.min(lastSavedPos.j(), curPos.j());
            int maxJ = Integer.max(lastSavedPos.j(), curPos.j());
            boolean actualObsDetected = false;
            boolean virtualObsDetected = false;
            for (int i = minI; i <= maxI; i++) {
                for (int j = minJ; j <= maxJ; j++) {
                    WPObstacleState curObsState = map
                            .getPoint(new Vector2(i, j))
                            .obstacleState();
                    if (curObsState.equals(WPObstacleState.IsActualObstacle)) {
                        actualObsDetected = true;
                        break;
                    } else if (curObsState.equals(WPObstacleState.IsVirtualObstacle)) {
                        virtualObsDetected = true;
                    }
                }
                if (actualObsDetected) {
                    break;
                }
            }

            // save if cur doesn't lie on the same line with last saved pos
            if (!(curPos.i() == lastSavedPos.i())
                    && !(curPos.j() == lastSavedPos.j())
                    && lastSavedTurnPos == null) {
                lastSavedTurnPos = curPos;
            }

            // if there is, save the prev pos
            if (actualObsDetected) {
                // check if prevPos & lastSavedPos are on the same row/col
                if ((prevPos.i() == lastSavedPos.i()
                        || prevPos.j() == lastSavedPos.j())
                        && virtualObsDetected) {
                    result.add(lastSavedTurnPos);
                }
                result.add(prevPos);
                lastSavedPos = prevPos;
                lastSavedTurnPos = null;
            }

            prevPos = curPos;
            
        }
        // save goal
        result.add(prevPos);

        return result;
    }

}
