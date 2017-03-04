package mdp.solver.exploration;

import mdp.map.Waypoint;
import mdp.robot.Robot;
import mdp.robot.RobotAction;

import java.io.IOException;

import mdp.Main;
import mdp.common.Direction;
import mdp.common.Vector2;
import mdp.map.Map;
import mdp.communication.*;


public class ActionFormulator {

	private static boolean simulation_mode = false;
	
    private MapViewer mapViewer;

    private Simulator simulator;

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

    public void rightWallFollower(Robot robot) throws InterruptedException, IOException {

        view(robot); // for scanning purpose

        if (null != mapViewer.checkWalkable(robot, Direction.Right)) {
            switch (mapViewer.checkWalkable(robot, Direction.Right)) {
                case Yes:
                    robot.bufferAction(RobotAction.RotateRight);
                    robot.bufferAction(RobotAction.MoveForward);
                    break;
                case No:
                    turnLeftTillEmpty(robot); //now didnt turn left , so execute directly
                    break;
                case Unsure:
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
                default:
                    break;
            }
        }

        // if all around empty, avoid turning in a loop, find the wall directly
        if (mapViewer.checkAllAroundEmpty(robot) == Know.Yes) {

            robot.bufferAction(RobotAction.RotateRight);
            robot.bufferAction(RobotAction.MoveForward);
        }

    }
    
    

    public static void sensingDataCallback(String input){
    		sensingDataFromRPI = input;
    		isSensingDataArrived = true;
    }
    
    // look through map and update 
    public Map view(Robot robot) throws InterruptedException, IOException {
        if (robot.checkIfHavingBufferActions()) {
            robot.executeBufferActions(ExplorationSolver.getExePeriod());
        }
        
        SensingData s = new SensingData(); // otherwise s may not have been initialized
        if(Main.getSimulationMode())
        		s = simulator.getSensingData(robot);
        else{
        		//RPI call here
        		Main.getRpi().sendSensingRequest();
        		while(isSensingDataArrived != true){ }
        			
                s.front_l= Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(0)));
                s.front_m= Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(1)));
                s.front_r= Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(2)));
                s.right_f= Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(3)));
                s.right_b= Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(4)));
                s.left= Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(5)));
        }
        
        
        
        Map subjective_map = mapViewer.updateMap(robot, s);
        System.out.println(mapViewer.exploredAreaToString());
        System.out.println(mapViewer.robotVisitedPlaceToString());
        System.out.println(subjective_map.toString(robot));
        isSensingDataArrived = false;
        return subjective_map;
    }

    public void circumvent(Robot robot) throws InterruptedException, IOException{
    		Vector2 initialPosition = robot.position();
    		while(robot.position().i()!= initialPosition.i() && robot.position().j()!= initialPosition.j())
    			{	
    				rightWallFollower(robot);
    				//System.out.println("Loop");
    			}
    }
    public void turnLeftTillEmpty(Robot robot) throws InterruptedException, IOException {

        Know check = mapViewer.checkWalkable(robot, Direction.Up);

        if (check == Know.Unsure) {
            view(robot);
        }
        // make sure it is viewed before turn
        //update
        check = mapViewer.checkWalkable(robot, Direction.Up);

        if (check == Know.Yes) {
            robot.bufferAction(RobotAction.MoveForward);
            return;
        }

        if (check == Know.No) {
            robot.bufferAction(RobotAction.RotateLeft);
            turnLeftTillEmpty(robot);

        }

    }
}
