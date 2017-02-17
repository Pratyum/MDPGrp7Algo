package mdp.solver.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import mdp.Direction;
import mdp.Map;
import mdp.Vector2;
import mdp.Robot;
import mdp.WPObstacleState;
import mdp.Waypoint;
import mdp.solver.Solver;

public class AStarSolver implements Solver {

    @Override
    public List<Vector2> solve(Map map, Robot robot) {
        List<Vector2> result = new ArrayList<Vector2>();
        
        // save points in map in a lookup hashtable
        Hashtable<String, AStarWaypoint> openedPoints = new Hashtable();
        Hashtable<String, AStarWaypoint> closedPoints = new Hashtable();
        
        // record robot position as cur point
        AStarWaypoint curPoint = new AStarWaypoint(new Waypoint(robot.position()));
        
        // loop until a point next tp goal is found
        while (AStarUtil.getMDistance(curPoint.position(), map.GOAL_POS) != 1) {
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
                        AStarUtil.getMDistance(adjPos, map.GOAL_POS),
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
            if (openedPoints.size() != 0) {
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
                System.out.println("NO SOLUTION");
                return result;
            }
        }
        
        // path has been found
        do {
            // save current point to result
            result.add(curPoint.position());
            
            // trace back the parent
            Vector2 parentDirection = curPoint.parentDir().toVector2();
            Vector2 parentPos = curPoint.position().fnAdd(parentDirection);
            
            // set cur to parent
            curPoint = closedPoints.get(parentPos.toString());
        } while (AStarUtil.getMDistance(curPoint.position(), robot.position()) != 0);
        Collections.reverse(result);
        
        return result;
    }

}