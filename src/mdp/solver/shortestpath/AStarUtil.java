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
//    	return 1;
    }

    public static int getMoveCost(Direction origin, Direction direction) {
//    	System.out.println(origin + " vs " + direction);
//    	System.out.println(direction == origin);
        if (direction == origin) {
            return 1;
        } else if (direction == origin.getBehind()) {
            return 3;
        } else {
            return 2;
        }
    }

    public static int getSmoothMoveCost(Direction origin, Direction direction) {
        if (direction == origin) {
            return 3;
        } else if (direction == origin.getBehind()) {
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
            boolean isTooRisky = false;
            for (int i = minI; i <= maxI; i++) {
                for (int j = minJ; j <= maxJ; j++) {
                    WPObstacleState curObsState = map
                            .getPoint(new Vector2(i, j))
                            .obstacleState();
                    if (!curObsState.equals(WPObstacleState.IsWalkable)) {
                        actualObsDetected = true;
                        break;
                    } else if (curObsState.equals(WPObstacleState.IsVirtualObstacle)) {
                        virtualObsDetected = true;
                    }
//                    if (Math.abs(maxI - i) > 3 || Math.abs(maxJ - j) > 3) {
//                        isTooRisky = true;
//                    }
                }
                if (actualObsDetected || isTooRisky) {
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
            if (actualObsDetected || isTooRisky) {
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

    public static int getSafetyBenefit(Map map, Vector2 curPos) {
        int result = 0;
        for (Direction dir : Direction.values()) {
            Vector2 adjPos = curPos.fnAdd(dir.toVector2());
            if (map.checkValidPosition(adjPos)) {
                if (!map.getPoint(adjPos).obstacleState()
                        .equals(WPObstacleState.IsWalkable)) {
                    result -= 1;
                }
            }
        }
        return result;
    }

}
