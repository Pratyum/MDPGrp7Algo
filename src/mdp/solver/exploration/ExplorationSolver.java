package mdp.solver.exploration;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.util.Pair;
import mdp.Main;
import mdp.common.Direction;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.map.WPSpecialState;
import mdp.robot.RobotAction;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class ExplorationSolver {

    private static Map objective_map; // generated from map solver

    private static Simulator simulator;

    private static MapViewer mapViewer = new MapViewer();
    private static ActionFormulator actionFormulator;
    private static GoalFormulator goalFormulator = new GoalFormulator(mapViewer);

    private static int _exePeriod;

    public static void main(Map map, int exePeriod) throws InterruptedException {
        _exePeriod = exePeriod;
        objective_map = map;
        simulator = new Simulator(objective_map);
        
        Vector2 robotPos = new Vector2(1, 1);
        Direction robotDir = Direction.Down;
        Robot robot = new Robot(robotPos, robotDir);
        actionFormulator = new ActionFormulator(mapViewer, simulator);
        
        AStarSolver astarSolver = new AStarSolver();
        LinkedList<RobotAction> robotActions;
        // put some blockers into the map
        System.out.println(objective_map.toString(robot));

        //data = getDataFromRPI();
        while (!goalFormulator.checkIfReachFinalGoal(robot.position())) {
            actionFormulator.rightWallFollower(robot);
        }
        while (!goalFormulator.checkIfReachStartZone(robot.position())) {

            actionFormulator.rightWallFollower(robot);

        }
        
        while(!mapViewer.checkIfNavigationComplete()){
        		Vector2 goal =mapViewer.findFurthestUnexplored(robot);
        		System.out.print("Goal:" +goal.toString());
        		AStarSolverResult astarSolverResult = astarSolver.solve(mapViewer.getSubjectiveMap(), robot , goal );
        		robotActions = RobotAction.fromPath(robot, astarSolverResult.shortestPath);
        		for (RobotAction action: robotActions){
        				view(robot);
        				
        				if(!mapViewer.validate(robot,action)){
        					actionFormulator.circumvent(robot);  
        					// in circumvent, stop circumventing when the obstacle is fully identified
        					break;
        				}
        				robot.bufferAction(action);
            }
        }
        
      
        /*(ArrayList<Vector2> unexplored = mapViewer.getUnExplored();
        //Print unexplored
        System.out.println("/////UnExplored////////");
        for(Vector2 coord: unexplored){
            System.out.println(coord);
        }
        System.out.println("//////////////////////");
        Vector2 start_coord = new Vector2(1,1);
        for(Vector2 coord: unexplored){
            coord.j(coord.j()+2);
            System.out.println("x "+ String.valueOf(coord.i())+" y "+ String.valueOf(coord.j()));
            AStarSolver solver = new AStarSolver();
            AStarSolverResult result = solver.solve(objective_map, robot, coord);
            if(result.shortestPath.size()!=0){
//            System.out.println("///////////////");
//            System.out.println(objective_map.toString(robot));
//            System.out.println(coord);
                Map hlMap = mapViewer.getMap();
                hlMap.highlight(result.shortestPath, WPSpecialState.IsPathPoint);
                Main.getGUI().update(hlMap, robot);
                LinkedList<RobotAction> robotactions = RobotAction.fromPath(robot,result.shortestPath);
                for (RobotAction action: robotactions){
                    robot.bufferAction(action);
                    view(robot);
                }
            }else{
                coord.j(coord.j()+1);
            }
       }
       AStarSolver solver = new AStarSolver();
       AStarSolverResult result = solver.solve(objective_map, robot, start_coord);
       LinkedList<RobotAction> robotactions = RobotAction.fromPath(robot,result.shortestPath);
       for (RobotAction action: robotactions){
            System.out.println(action);
            robot.bufferAction(action);
            view(robot);
       }
        System.out.println("i:" + robot.position().i());
        System.out.println("j:" + robot.position().j());
		*/
    }

    public static int getExePeriod() {
        return _exePeriod;
    }

    public static MapViewer getMapViewer() {
        return mapViewer;
    }
    
        // look through map and update 
    public static Map view(Robot robot) throws InterruptedException {
        if (robot.checkIfHavingBufferActions()) {
            robot.executeBufferActions(ExplorationSolver.getExePeriod());
        }

        SensingData s;
        s = simulator.getSensingData(robot);
        Map subjective_map = mapViewer.updateMap(robot, s);
        System.out.println(mapViewer.exploredAreaToString());
        System.out.println(subjective_map.toString(robot));

        return subjective_map;
    }
}
