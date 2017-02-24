package mdp.solver.exploration;
import java.util.ArrayList;


import java.util.List;
import mdp.Waypoint;
import mdp.Direction;
import mdp.Map;
import mdp.Robot;
import mdp.Vector2;
import mdp.WPObstacleState;
import mdp.solver.exploration.MapViewer;
import mdp.RobotAction;
import java.util.LinkedList;

public class ExplorationSolver {
    private static Map objective_map = new Map(); // generated from map solver
    private static Map subjective_map = new Map();
    private Robot robot ;
    private static LinkedList<RobotAction> robotActions= new LinkedList<>() ;
    private static Simulator simulator = new Simulator();
    
    private static MapViewer mapViewer = new MapViewer();

    private static int[][] _map = 
        {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };


   
	public static void main(String[] args){
		
        Vector2 robotPos = new Vector2(1, 1);
        Direction robotDir = Direction.Down;
        Robot robot = new Robot(robotPos, robotDir);
        
        SensingData s; 
        int x;
        
        // put some blockers into the map
        objective_map.addObstacle(_genBlockers(_map));//integrate
        
        simulator.initializeMap(objective_map);
        
        System.out.println(objective_map.toString(robot));
        
        
               
	    	
	    		
	    		
	    		//data = getDataFromRPI();
        for(x=0;x<100 ;x++){
        ///    
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
	    	    	
	    	    	// make a call to simulator
	    	    	
        }
        		
	    			
	}
    private static List<Vector2> _genBlockers(int[][] obstacleMap) {
        List<Vector2> blockers = new ArrayList<>();
        for (int i = 0; i < obstacleMap.length; i++) {
            for (int j = 0; j < obstacleMap[0].length; j++) {
                if (obstacleMap[i][j] == 1) {
                    blockers.add(new Vector2(i, j));
                }
            }
        }
        return blockers;
    }
    
    
    // look through map and update 
    public static Map view(Robot robot){
        
    		SensingData s;
    		s= simulator.getSensingData(robot); 		
	    	subjective_map= mapViewer.updateMap(robot , s);
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
