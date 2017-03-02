package mdp.solver.shortestpath;

import java.util.Collections;
import java.util.HashMap;
import mdp.common.Direction;
import mdp.map.Map;
import mdp.common.Vector2;
import mdp.robot.Robot;
import mdp.map.WPObstacleState;
import mdp.map.Waypoint;

public class AStarSolver {

    public Vector2 solveSingleHeuristic(Map map, Robot robot, Vector2 goalPos) {
        Vector2 diff = goalPos.fnAdd(robot.position().fnMultiply(-1));
        Vector2 result;
        if (diff.j() > diff.i()) {
            // move horizontally
            if (diff.j() > 0) {
                // move down
                result = robot.position().fnAdd(Direction.Down.toVector2());
            } else {
                // move up
                result = robot.position().fnAdd(Direction.Up.toVector2());
            }
        } else {
            // move vertically
            if (diff.i() > 0) {
                // move right
                result = robot.position().fnAdd(Direction.Right.toVector2());
            } else {
                // move left
                result = robot.position().fnAdd(Direction.Left.toVector2());
            }
        }
        return result;
    }

    public AStarSolverResult solve(Map map, Robot robot, Vector2 goalPos) {

        AStarSolverResult result = new AStarSolverResult();

        // save points in map in a lookup hashtable
        HashMap<String, AStarWaypoint> openedPoints = new HashMap();
        HashMap<String, AStarWaypoint> closedPoints = new HashMap();

        // record robot position as cur point
        AStarWaypoint curPoint = new AStarWaypoint(new Waypoint(robot.position()));

        // check if cur point is goal
        if (curPoint.position().equals(goalPos)) {
            System.out.println("Robot already at goal.");
            return result;
        }

        // loop until a point next tp goal is found
        while (AStarUtil.getMDistance(curPoint.position(), goalPos) != 1) {
//            System.out.println("Cur:");
//            System.out.println(curPoint.position());

            // close point
            openedPoints.remove(curPoint.position().toString());
            closedPoints.put(curPoint.position().toString(), curPoint);

            // detect adj points
//            System.out.println("Finding adj:");
            Vector2 curPos = curPoint.position();
            for (Direction dir : Direction.values()) {
//                System.out.println(dir.toString());
                // get adj point
                Vector2 adjPos = curPos.fnAdd(dir.toVector2());
                if (map.checkValidPosition(adjPos)) {
//                    System.out.println(adjPos);
                    Waypoint adjMapPoint = map.getPoint(adjPos);
                    AStarWaypoint adjPoint = new AStarWaypoint(
                            adjMapPoint,
                            AStarUtil.getMDistance(adjPos, goalPos),
                            AStarUtil.getMoveCost(robot, dir) + curPoint.gval(),
                            dir.getBehind()
                    );

                    // check if adj is closed or already opened
                    String adjKey = adjPoint.position().toString();
                    if (!closedPoints.containsKey(adjKey)) {
                        if (openedPoints.containsKey(adjKey)) {
                            // point already in opened, update if possible
                            AStarWaypoint oldPoint = openedPoints.get(adjKey);
                            if (adjPoint.fval() < oldPoint.fval()) {
                                openedPoints.replace(adjKey, adjPoint);
                            }
                        } else {
                            // check if adj is walkable
                            if (adjPoint.obstacleState() == WPObstacleState.IsWalkable) {
                                // if yes then save new point info
                                openedPoints.put(adjKey, adjPoint);
                            }
                        }
                    }
                }
            }

            // check all open points for potential next cur point
            if (!openedPoints.isEmpty()) {
                // loop through map info to find lowest fval point
                AStarWaypoint lowestFvalPoint = new AStarWaypoint();
                for (String key : openedPoints.keySet()) {
                    AStarWaypoint cur = openedPoints.get(key);
                    if (cur.fval() < lowestFvalPoint.fval()) {
                        lowestFvalPoint = cur;
                    }
                }

                // set this as cur
//                System.out.println("Next cur selected:");
//                System.out.println(lowestFvalPoint.position());
//                System.out.println();
                curPoint = lowestFvalPoint;
            } else {
                // path to goal is blocked
                System.out.println("No possible path to goal found.");
                return result;
            }
        }

        // path has been found
        result.shortestPath.add(goalPos);
        do {
            // save current point to result
            result.shortestPath.add(curPoint.position());

            // trace back the parent
            Vector2 parentDirection = curPoint.parentDir().toVector2();
            Vector2 parentPos = curPoint.position().fnAdd(parentDirection);

            // set cur to parent
            curPoint = closedPoints.get(parentPos.toString());
        } while (AStarUtil.getMDistance(curPoint.position(), robot.position()) != 0);
        Collections.reverse(result.shortestPath);

        // add opened & closed to result
        openedPoints.forEach((pointKey, point) -> {
            result.openedPoints.add(point.position());
        });
        closedPoints.forEach((pointKey, point) -> {
            result.closedPoints.add(point.position());
        });

        return result;

    }

    public AStarSolverResult solve(Map map, Robot robot) {
        return solve(map, robot, map.GOAL_POS);
    }

}
