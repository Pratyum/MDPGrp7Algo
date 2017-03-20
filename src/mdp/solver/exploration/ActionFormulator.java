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
            boolean ventureIntoDangerousZone = false;
            boolean dangerousZoneEntered = false;
            
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
                
                System.out.println(robotActions.toString());
                //System.out.println("Action size: " + robotActions.size());
                
                
                for (int i=0; i<robotActions.size() ; i++) {
                    Robot robotSimulator = new Robot(new Vector2(_robot.position().i(),_robot.position().j()),_robot.orientation());
                    if(i>=1)
                        robotSimulator.execute(robotActions.get(i-1));
                    
                    if(ventureIntoDangerousZone == false && !mapViewer.checkIfInDangerousZone(robotSimulator) ){
                        robotSimulator.execute(robotActions.get(i));
                        if(mapViewer.checkIfInDangerousZone(robotSimulator)){
                        
                        ventureIntoDangerousZone = true;
                        
                        System.out.println("Venturing into somewhere dangerous");
                        if(!Main.isSimulating())
                            Main.getRpi().sendCalibrationCommand(CalibrationType.Emergency);
                        }
                    }
                    
                    Robot robotSimulator2 = new Robot(new Vector2(_robot.position().i(),_robot.position().j()),_robot.orientation());
                    if(i>=1)
                        robotSimulator2.execute(robotActions.get(i-1));
                    
                    
                    if(mapViewer.checkLeftObstacles(robotSimulator2))
                        if(Main.isSimulating())
                           System.out.println("Send calibration command " + CalibrationType.Left.toString()); 
                        else 
                           Main.getRpi().sendCalibrationCommand(CalibrationType.Left);
                    
                    //only front back on the right side
                    if(mapViewer.checkRightFrontBack(robotSimulator2))
                        if(Main.isSimulating())
                            System.out.println("Send calibration command " + CalibrationType.Right.toString()); 
                        else
                           Main.getRpi().sendCalibrationCommand(CalibrationType.Right);
                     
                    
                    
                    
                    
                    // for actions , check next move and give calibration command
                    System.out.println(_robot.position().toString());
                    System.out.println(_robot.orientation().toString());
                    view(_robot);
                    
                    if(mapViewer.checkIfInDangerousZone(_robot)){
                        dangerousZoneEntered = true;
                    }
                    
                    
                    if( dangerousZoneEntered && ventureIntoDangerousZone && !mapViewer.checkIfInDangerousZone(_robot)){
                        dangerousZoneEntered= false;
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

    public void actionSimplifier(Robot _robot) throws InterruptedException, IOException {
        // TODO Auto-generated method stub
        if(!turnAroundInDeadCorner(_robot));
        //    turnAroundFromRight(_robot);
    }
    
    
    private void turnAroundFromRight(Robot _robot) {
        // TODO Auto-generated method stub
        
    }

    public boolean turnAroundInDeadCorner(Robot _robot) throws InterruptedException, IOException{
        Vector2 left_m,left_f,left_b, right_up, right_down, right_middle,front_m,front_l,front_r;
        boolean frontWall;
        boolean right_up_explored, right_down_explored,right_middle_explored;
        boolean left_m_explored, left_f_explored, left_b_explored;
        
        frontWall = false;
        right_up_explored =false;
        right_down_explored= false;
        right_middle_explored = false;
        
        left_m_explored= false;
        left_f_explored= false;
        left_b_explored=false;
        
        
        left_m = _robot.position().fnAdd(_robot.orientation().getLeft().toVector2().fnMultiply(2));
        left_f = left_m.fnAdd(_robot.orientation().toVector2());
        left_b = left_m.fnAdd(_robot.orientation().getBehind().toVector2());
        
        right_up = _robot.position().fnAdd(_robot.orientation().toVector2()).fnAdd(_robot.orientation().getRight().toVector2().fnMultiply(2));
        right_down = _robot.position().fnAdd(_robot.orientation().getBehind().toVector2()).fnAdd(_robot.orientation().getRight().toVector2().fnMultiply(2));
        right_middle= _robot.position().fnAdd(_robot.orientation().getRight().toVector2().fnMultiply(2));
        
        
        front_m = _robot.position().fnAdd(_robot.orientation().toVector2().fnMultiply(2));
        front_l = front_m.fnAdd(_robot.orientation().getLeft().toVector2());
        front_r = front_m.fnAdd(_robot.orientation().getRight().toVector2());
        
        
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(front_r) &&
                !mapViewer.getSubjectiveMap().checkValidBoundary(front_l) &&
                !mapViewer.getSubjectiveMap().checkValidBoundary(front_m)){ 
            frontWall = true;
        }
        
        
        //
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(right_up))
            right_up_explored= true;
        else if (mapViewer.getSubjectiveMap().getPoint(right_up).obstacleState() == WPObstacleState.IsActualObstacle){
            right_up_explored= true;
        }
        else if(mapViewer.getExploredState(right_up)!=0){
            right_up = right_up.fnAdd(_robot.orientation().getRight().toVector2());
            if(!mapViewer.getSubjectiveMap().checkValidBoundary(right_up))
                right_up_explored= true;
            else if (mapViewer.getSubjectiveMap().getPoint(right_up).obstacleState() == WPObstacleState.IsActualObstacle){
                right_up_explored= true;
            }
        }
        
        //
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(right_down))
            right_down_explored= true;
        else if (mapViewer.getSubjectiveMap().getPoint(right_down).obstacleState() == WPObstacleState.IsActualObstacle){
            right_down_explored= true;
        }
        else if(mapViewer.getExploredState(right_down)!=0){
            right_down = right_down.fnAdd(_robot.orientation().getRight().toVector2());
            if(!mapViewer.getSubjectiveMap().checkValidBoundary(right_down))
                right_down_explored= true;
            else if (mapViewer.getSubjectiveMap().getPoint(right_down).obstacleState() == WPObstacleState.IsActualObstacle){
                right_down_explored= true;
            }
        }
        
        
        //
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(right_middle))
            right_middle_explored= true;
        else if (mapViewer.getSubjectiveMap().getPoint(right_middle).obstacleState() == WPObstacleState.IsActualObstacle){
            right_middle_explored= true;
        }
        else if(mapViewer.getExploredState(right_middle)!=0){
            right_middle = right_middle.fnAdd(_robot.orientation().getRight().toVector2());
            if(!mapViewer.getSubjectiveMap().checkValidBoundary(right_middle))
                right_middle_explored= true;
            else if (mapViewer.getSubjectiveMap().getPoint(right_middle).obstacleState() == WPObstacleState.IsActualObstacle){
                right_middle_explored= true;
            }
        }
        
        
        //
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(left_f))
            left_f_explored= true;
        else if (mapViewer.getSubjectiveMap().getPoint(left_f).obstacleState() == WPObstacleState.IsActualObstacle){
            left_f_explored= true;
        }
        else if(mapViewer.getExploredState(left_f)!=0){
            left_f = left_f.fnAdd(_robot.orientation().getLeft().toVector2());
            if(!mapViewer.getSubjectiveMap().checkValidBoundary(left_f))
                left_f_explored= true;
            else if (mapViewer.getSubjectiveMap().getPoint(left_f).obstacleState() == WPObstacleState.IsActualObstacle){
                left_f_explored= true;
            }
        }
        
        
        //
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(left_m))
            left_m_explored= true;
        else if (mapViewer.getSubjectiveMap().getPoint(left_m).obstacleState() == WPObstacleState.IsActualObstacle){
            left_m_explored= true;
        }
        else if(mapViewer.getExploredState(left_m)!=0){
            left_m = left_m.fnAdd(_robot.orientation().getLeft().toVector2());
            if(!mapViewer.getSubjectiveMap().checkValidBoundary(left_m))
                left_m_explored= true;
            else if (mapViewer.getSubjectiveMap().getPoint(left_m).obstacleState() == WPObstacleState.IsActualObstacle){
                left_m_explored= true;
            }
        }
        
        
        //
        if(!mapViewer.getSubjectiveMap().checkValidBoundary(left_b))
            left_b_explored= true;
        else if (mapViewer.getSubjectiveMap().getPoint(left_b).obstacleState() == WPObstacleState.IsActualObstacle){
            left_b_explored= true;
        }
        else if(mapViewer.getExploredState(left_b)!=0){
            left_b = left_b.fnAdd(_robot.orientation().getLeft().toVector2());
            if(!mapViewer.getSubjectiveMap().checkValidBoundary(left_b))
                left_b_explored= true;
            else if (mapViewer.getSubjectiveMap().getPoint(left_b).obstacleState() == WPObstacleState.IsActualObstacle){
                left_b_explored= true;
            }
        }
        
        
        if(left_b_explored && left_f_explored &&left_m_explored &&
                right_middle_explored && right_up_explored & right_down_explored &&
                frontWall){
            _robot.bufferAction(RobotAction.RotateLeft);
            _robot.bufferAction(RobotAction.RotateLeft);
            
            _robot.bufferAction(RobotAction.MoveForward);
            view(_robot);
            if(mapViewer.checkWalkable(_robot, Direction.Right)== Know.Yes){
                _robot.bufferAction(RobotAction.RotateRight);
                _robot.bufferAction(RobotAction.MoveForward);
                _robot.bufferAction(RobotAction.RotateLeft);
                view(_robot);
            }
            return true;
        }
        else
            return false;
    }
    
    
    
}
