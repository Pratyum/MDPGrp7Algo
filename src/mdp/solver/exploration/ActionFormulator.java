package mdp.solver.exploration;

import mdp.map.Waypoint;
import mdp.robot.Robot;
import mdp.robot.RobotAction;

import java.io.IOException;
import java.util.LinkedList;

import mdp.Main;
import mdp.common.Direction;
import mdp.common.Vector2;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.communication.*;
import mdp.solver.shortestpath.*;

public class ActionFormulator {

    private static boolean simulation_mode = false;

    private MapViewer mapViewer;

    private Simulator simulator;

    private static volatile boolean calibrationCompleted = false;
    private static volatile boolean isSensingDataArrived = false;
    private static volatile String sensingDataFromRPI;

    public ActionFormulator(MapViewer mapV, Simulator s) {
        mapViewer = mapV;
        simulator = s;
    }

    //got to init map . can we inject map instead?
    public String actionToTake(Waypoint frontier, Robot robot) {

        String result = "";

        return result;

    }

    public void reverseToThePoint(RobotMovementHistory robotState, Robot robot) throws InterruptedException, IOException {
        AStarSolver astarsolver = new AStarSolver();
        AStarSolverResult astarSolverResult = astarsolver.solve(mapViewer.getSubjectiveMap(), robot, robotState._position);
        LinkedList<RobotAction> robotActions = RobotAction.fromPath(robot, astarSolverResult.shortestPath);
        //System.out.println("Action size: " + robotActions.size());
        robotActions.forEach((action) -> {
            //System.out.println("Here3");
            robot.bufferAction(action);
        });
        view(robot);

        while (robot.orientation() != robotState._direcition) {
            robot.bufferAction(RobotAction.RotateLeft);
            view(robot);
        }

    }

    public void exploreRemainingArea(Robot _robot) throws InterruptedException, IOException {
        boolean reachablePointFound = false;
        LinkedList<Vector2> reachableList;
        AStarSolver astarSolver = new AStarSolver();
        LinkedList<RobotAction> robotActions;

        while (!mapViewer.checkIfNavigationComplete()) {
            LinkedList<Vector2> goalList = mapViewer.findUnexploredInAscendingDistanceOrder(_robot);
            System.out.println("My goal list  " + goalList.toString());
            Vector2 goal = new Vector2(-1, -1);
            
            for (int i = 0; i < goalList.size(); i++) {

                System.out.println("Processing goal " + goalList.get(i).toString());
                if (mapViewer.markGhostBlock(goalList.get(i))) {
                    continue;
                }
                reachableList = mapViewer.findScannableReachableFromGoal(goalList.get(i), _robot);
                for (int j = 0; j < reachableList.size(); j++) {
                    if (!mapViewer.checkRobotVisited(reachableList.get(j)))
                        {

                        goal = reachableList.get(j);
                        reachablePointFound = true;
                        System.out.println("Goal found " + goal);
                        break;
                    }
                }

                if (reachablePointFound) {
                    break; /// findFirst goal
                }
                mapViewer.markUnreachable(goalList.get(i));
            }

            if (reachablePointFound) {

                System.out.println("Current goal: " + goal.toString());

                AStarSolverResult astarSolverResult = astarSolver.solve(mapViewer.getSubjectiveMap(), _robot, goal);

                robotActions = RobotAction.fromPath(_robot, astarSolverResult.shortestPath);
                System.out.println(_robot.position().toString());
                System.out.println(_robot.orientation().toString());
                System.out.println(robotActions.toString());
                //System.out.println("Action size: " + robotActions.size());
                boolean ventureIntoDangerousZone = false;
                boolean dangerousZoneEntered = false;
                
                for (int i=0; i<robotActions.size() ; i++) {
                    Robot robotSimulator = new Robot(_robot.position(),_robot.orientation());
                    robotSimulator.execute(robotActions.get(i));
                    robotSimulator.execute(robotActions.get(i+1));
                    if(ventureIntoDangerousZone == false && mapViewer.checkIfInDangerousZone(robotSimulator)){
                        
                        ventureIntoDangerousZone = true;
                        
                        System.out.println("Venturing into somewhere dangerous");
                        if(!Main.isSimulating())
                            Main.getRpi().sendCalibrationCommand(CalibrationType.Emergency);
                    }
                    
                    
                     
                    
                    
                    
                    
                    // for actions , check next move and give calibration command
                    view(_robot);
                    
                    if(mapViewer.checkIfInDangerousZone(_robot)){
                        dangerousZoneEntered = true;
                    }
                    
                    
                    if( dangerousZoneEntered && ventureIntoDangerousZone && !mapViewer.checkIfInDangerousZone(_robot)){
                        ventureIntoDangerousZone = false;
                    }
                    
                    
                    if (!mapViewer.validate(_robot, robotActions.get(i))) {
                        //actionFormulator.circumvent(_robot);
                        //System.out.println("Here2");
                        // in circumvent, stop circumventing when the obstacle is fully identified
                        view(_robot); // take a look , update map
                        break;
                    }
                    //System.out.println("Here3");
                    System.out.println("Execute action"+ robotActions.get(i));
                    _robot.bufferAction(robotActions.get(i));

                }
            }

            // prepare for next loop
            reachablePointFound = false;
        }
        System.out.println("All remaining blocks explored or checked as unreachable ");
        
        System.out.println("Exploration completed");
        
    }

    public void rightWallFollower(Robot robot) throws InterruptedException, IOException {

        //can I view less ? 
        view(robot); // for scanning purpose

        /*while(mapViewer.checkIfRight5SquaresEmpty(robot)){
        		robot.bufferAction(RobotAction.MoveBackward);
        		view(robot);
        }*/
        if (null != mapViewer.checkWalkable(robot, Direction.Right)) {

            switch (mapViewer.checkWalkable(robot, Direction.Right)) {
                case Yes:
                    robot.bufferAction(RobotAction.RotateRight);
                    view(robot);
                    robot.bufferAction(RobotAction.MoveForward);
                    break;
                case No:
                    turnLeftTillEmpty(robot); //now didnt turn left , so execute directly
                    break;
                case Unsure: {
                    robot.bufferAction(RobotAction.RotateRight);
                    view(robot);
                    if (null == mapViewer.checkWalkable(robot, Direction.Up)) {
                        System.out.println("Error1");
                    } else {
                        switch (mapViewer.checkWalkable(robot, Direction.Up)) {
                            case Yes:
                                robot.bufferAction(RobotAction.MoveForward);
                                break;
                            case No:
                                robot.bufferAction(RobotAction.RotateLeft);
                                turnLeftTillEmpty(robot);
                                break;
                            default:
                                System.out.println("Error1");
                                break;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
        view(robot);

        // if all around empty, avoid turning in a loop, find the wall directly
        /*if (mapViewer.checkAllAroundEmpty(robot) == Know.Yes) {
        		System.out.println("All around empty");
            robot.bufferAction(RobotAction.RotateRight);
            
            while(mapViewer.checkWalkable(robot, Direction.Up) == Know.Yes){
            		robot.bufferAction(RobotAction.MoveForward);
            }
            robot.bufferAction(RobotAction.RotateLeft);
            view(robot);
        }*/
    }

    public static void sensingDataCallback(String input) {
        sensingDataFromRPI = input;

    }

    // look through map and update 
    public Map view(Robot robot) throws InterruptedException, IOException {
        int calibirationType;
        
        ///check current map configuration , give calibration command
        
        if(mapViewer.checkLeftObstacles(robot))
            if(Main.isSimulating())
               System.out.println("Send calibration command " + CalibrationType.Left.toString()); 
            else 
               Main.getRpi().sendCalibrationCommand(CalibrationType.Left);
        
        //only front back on the right side
        if(mapViewer.checkRightFrontBack(robot))
            if(Main.isSimulating())
                System.out.println("Send calibration command " + CalibrationType.Right.toString()); 
            else
               Main.getRpi().sendCalibrationCommand(CalibrationType.Right);
        
        if (robot.checkIfHavingBufferActions()) {
            robot.executeBufferActions(ExplorationSolver.getExePeriod());
        }

        SensingData s = new SensingData(); // otherwise s may not have been initialized
        if (Main.isSimulating()) {
            s = simulator.getSensingData(robot);
        } else {

            //RPI call here
            //while (isSensingDataArrived != true) {}
            if (sensingDataFromRPI.isEmpty()) {
                System.out.println("ERROR: empty sensing data");
            }

            s.front_l = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(0)));
            s.front_m = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(1)));
            s.front_r = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(2)));
            s.right_f = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(3)));
            s.right_m = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(4)));
            s.left = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(5)));

        }

        Map subjective_map = mapViewer.updateMap(robot, s);
        System.out.println(mapViewer.exploredAreaToString());
        //System.out.println(mapViewer.robotVisitedPlaceToString());
        //System.out.println(mapViewer.confidenceDetectionAreaToString());
        System.out.println(subjective_map.toString(robot));

        isSensingDataArrived = false;

        /*if (!Main.isSimulating()) {
            if(robot.checkIfCalibrationCounterReached()){
            	
            		CalibrationType ct= mapViewer.checkCalibrationAvailable(robot);
            		switch(ct){
                    case NA:
                    		break;
                    	default:
                    		System.out.println("send calibration "+ ct.toString());
                    		Main.getRpi().sendCalibrationCommand(ct);
                    		while(!calibrationCompleted){}
                    		robot.clearCalibrationCounter();
                    		break;
                }
            		
            		
   
            }
            ///
            
            calibrationCompleted = false;
        }
         */
        return subjective_map;
    }

    public static void calibrationCompletedCallBack() {
        calibrationCompleted = true;
    }

    public void circumvent(Robot robot) throws InterruptedException, IOException {
        Vector2 initialPosition = robot.position();
        while (robot.position().i() != initialPosition.i() && robot.position().j() != initialPosition.j()) {
            //rightWallFollower(robot);
            //System.out.println("Loop");
        }
    }

    public void turnLeftTillEmpty(Robot robot) throws InterruptedException, IOException {

        Know check = mapViewer.checkWalkable(robot, Direction.Up);

        /*if (check == Know.Unsure) { */
        view(robot);
        /*}*/
        // make sure it is viewed before turn
        //update
        check = mapViewer.checkWalkable(robot, Direction.Up);

        if (check == Know.Yes) {
            robot.bufferAction(RobotAction.MoveForward);
            return;
        }

        if (check == Know.No) {
            robot.bufferAction(RobotAction.RotateLeft);
            view(robot);
            turnLeftTillEmpty(robot);

        }

    }
}
