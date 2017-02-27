package mdp.solver.exploration;
import mdp.map.Waypoint ;
import mdp.robot.Robot;
import mdp.robot.RobotAction;

import java.util.LinkedList;

import mdp.common.Direction;
import mdp.map.Map;
public class ActionFormulator {

	private static LinkedList<RobotAction> robotActions= new LinkedList<>() ;
	private static MapViewer mapViewer;

	private static Simulator simulator;
	public ActionFormulator(MapViewer mapV , Simulator s){
		mapViewer = mapV;
		simulator = s;
	}
	
	//got to init map . can we inject map instead?
	
	public String actionToTake(Waypoint frontier, Robot robot ){

		String result = "";
                
                return result;
		
	}

	public LinkedList<RobotAction> rightWallFollower(Robot robot){
    		robotActions.clear();
    	
	    	view(robot);
	    	
	    	    	if(mapViewer.checkWalkable(robot, Direction.Right)==Know.Yes){
	    	    		robot.execute(RobotAction.RotateRight);robotActions.add(RobotAction.RotateRight);
	    	    		robot.execute(RobotAction.MoveForward);robotActions.add(RobotAction.MoveForward);
	    	    	}
	    	    	else if (mapViewer.checkWalkable(robot, Direction.Right)==Know.No){
	    	    		turnLeftTillEmpty(robot); //now didnt turn left , so execute directly
	    	    	}
	    	    	else if (mapViewer.checkWalkable(robot, Direction.Right)==Know.Unsure){
	    	    		robot.execute(RobotAction.RotateRight);robotActions.add(RobotAction.RotateRight);
	    	    		
	    	    		view(robot);
	    	    		
	
	    		    if (mapViewer.checkWalkable(robot, Direction.Up)==Know.Yes){
	    		    		robot.execute(RobotAction.MoveForward);robotActions.add(RobotAction.MoveForward);
	    		    	}
	    		    else if(mapViewer.checkWalkable(robot, Direction.Up)==Know.No){
	    		    		robot.execute(RobotAction.RotateLeft);robotActions.add(RobotAction.RotateLeft);
	    		    		turnLeftTillEmpty(robot); 
	    		    }
	    		    	else
	    		    		System.out.println("Error1");
	    	    	}
	    	    
	    	    	// if all around empty, avoid turning in a loop, find the wall directly
	    	    	if(mapViewer.checkAllAroundEmpty(robot)==Know.Yes){
	    	    		/*switch(robot.direction()){
	    	    		case Right:
	    	    			robot.execute(RobotAction.RotateLeft);
	    	    			robot.execute(RobotAction.RotateLeft);
	    	    			break;
	    	    		case Left:
	    	    			break;
	    	    		case Up:
	    	    			robot.execute(RobotAction.RotateLeft);
	    	    			break;
	    	    		case Down:
	    	    			robot.execute(RobotAction.RotateRight);
	    	    			break;
	    	    
	    	    	
	    	    		} */
	    	    		//go find a wall
	    	    
		    	    /*	while(mapViewer.checkWalkable(robot, Direction.Up)!=0 ){
	    	    			robot.execute(RobotAction.MoveForward);
	    	    			if(mapViewer.checkWalkable(robot, Direction.Up)==2)
	    	    				view(robot);
	    	    			}
	    	    		
		    	    	robot.execute(RobotAction.RotateLeft);
	    	    		*/
	    	    		robot.execute(RobotAction.RotateRight);robotActions.add(RobotAction.RotateRight);
	    	    		robot.execute(RobotAction.MoveForward);robotActions.add(RobotAction.MoveForward);
	    	    	}
		
    	    	return robotActions;
	}
	
	   // look through map and update 
    public static Map view(Robot robot){
        
    		SensingData s;
    		s= simulator.getSensingData(robot); 		
	    Map	subjective_map= mapViewer.updateMap(robot , s);
	    	System.out.println(mapViewer.exploredAreaToString());
	    	System.out.println(subjective_map.toString(robot));
    	
	    	return subjective_map;
    }
    
    public static void turnLeftTillEmpty(Robot robot){
    	
    		Know check = mapViewer.checkWalkable(robot, Direction.Up);

    		if(check==Know.Unsure){
    			view(robot);
    		}
    		// make sure it is viewed before turn
    		//update
    		check = mapViewer.checkWalkable(robot, Direction.Up);
    		
    		if(check==Know.Yes){
    			robot.execute(RobotAction.MoveForward);robotActions.add(RobotAction.MoveForward);
    			return;
    		}
    		
    		
    		if(check==Know.No){
    			robot.execute(RobotAction.RotateLeft);robotActions.add(RobotAction.RotateLeft);
    			turnLeftTillEmpty( robot);
    			
    		}
    		
    	
    }
    
    
	
	
}
