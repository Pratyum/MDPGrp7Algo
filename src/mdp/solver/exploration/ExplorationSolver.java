package mdp.solver.exploration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import mdp.Main;
import mdp.common.Direction;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.robot.RobotAction;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class ExplorationSolver {
    
//    public enum EndType { Normal, Interrupt }

    private static Map objective_map; // generated from map solver

    private static Simulator simulator;

    private static MapViewer mapViewer;
    private static ActionFormulator actionFormulator;
    private static GoalFormulator goalFormulator;

    private static int _exePeriod;
    private static Robot _robot;

    public static void solve(Map map, int exePeriod) throws InterruptedException, IOException {
        mapViewer = new MapViewer();
        goalFormulator = new GoalFormulator(mapViewer);
        _exePeriod = exePeriod;
        objective_map = map;
        simulator = new Simulator(objective_map);
        
        Vector2 robotPos = new Vector2(1, 1);
        Direction robotDir = Direction.Right;
        _robot = new Robot(robotPos, robotDir, mapViewer);
        actionFormulator = new ActionFormulator(mapViewer, simulator);
        boolean reachablePointFound = false;
        AStarSolver astarSolver = new AStarSolver();
        LinkedList<RobotAction> robotActions;
        LinkedList<Vector2> reachableList;
        
        // put some blockers into the map
        System.out.println(objective_map.toString(_robot));
        
        
        //data = getDataFromRPI();
        System.out.println(_robot.position());
        
        //default to rotate in order to get initial sensing data
        _robot.bufferAction(RobotAction.RotateRight);
        _robot.executeBufferActions(ExplorationSolver.getExePeriod());;
        ////////////
        //start exploration
        	while (!goalFormulator.checkIfReachFinalGoal(_robot.position())) {
            System.out.println("following right wall");
            actionFormulator.rightWallFollower(_robot);
        }
        while (!goalFormulator.checkIfReachStartZone(_robot.position())) {
            System.out.println("following right wall");
            actionFormulator.rightWallFollower(_robot);

        }

        while (!mapViewer.checkIfNavigationComplete()) {
            LinkedList<Vector2> goalList = mapViewer.findUnexploredInAscendingDistanceOrder(_robot);
            System.out.println("My goal list  "+goalList.toString());
            Vector2 goal = new Vector2(-1,-1);
            if(goalList.size()==0)
            {
            		System.out.println("Exploration completed");
            		break;
            }
            for(int i=0 ; i< goalList.size(); i++){
            		
            		System.out.println("Processing goal "+goalList.get(i).toString());
            		if(mapViewer.markGhostBlock(goalList.get(i)))
            			continue;
            		reachableList = mapViewer.findScannableReachableFromGoal(goalList.get(i), _robot);
            		for(int j = 0 ; j < reachableList.size(); j++){
            			if(!mapViewer.checkRobotVisited(reachableList.get(j)))
            			{
            				
            				goal = reachableList.get(j);
            				reachablePointFound = true;	
            				System.out.println("Goal found "+goal);
            				break;
            			}
            		}
            		
            		if(reachablePointFound)
            			break; /// findFirst goal
            		mapViewer.markUnreachable(goalList.get(i));
            }
            
            if(reachablePointFound){

                System.out.println("Current goal: "+goal.toString());
                
                AStarSolverResult astarSolverResult = astarSolver.solve(mapViewer.getSubjectiveMap(), _robot, goal);
                
                robotActions = RobotAction.fromPath(_robot, astarSolverResult.shortestPath);
                //System.out.println("Action size: " + robotActions.size());
                for (RobotAction action : robotActions) {
                    view(_robot);

                    if (!mapViewer.validate(_robot, action)) {
                        //actionFormulator.circumvent(_robot);
                        //System.out.println("Here2");
                        // in circumvent, stop circumventing when the obstacle is fully identified
                        actionFormulator.view(_robot); // take a look , update map
                    		break;
                    }
                    //System.out.println("Here3");
                    _robot.bufferAction(action);
                }
            }
            
            // prepare for next loop
            reachablePointFound = false;
        }
        
        
    }

    public static int getExePeriod() {
        return _exePeriod;
    }

    public static MapViewer getMapViewer() {
        return mapViewer;
    }

    public static Robot getRobot() {
        return _robot;
    }

    // look through map and update 
    public static Map view(Robot robot) throws InterruptedException, IOException {
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

    public static void goBackToStart(Map map, Robot robot, Runnable callback) {
        System.out.println("Going back to start with the following map");
        System.out.println(map.toString(robot));
        AStarSolverResult result = new AStarSolver().solve(map, robot, Map.START_POS);
        LinkedList<RobotAction> actions = RobotAction.fromPath(robot, result.shortestPath);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!actions.isEmpty()) {
                    RobotAction action = actions.pop();
                    robot.execute(action);
                    Main.getGUI().update(robot);
                } else {
                    timer.cancel();
                    System.out.println("Starting callback");
                    callback.run();
                }
            }
        }, _exePeriod, _exePeriod);
    }

    public static void restart() {
    }
}
