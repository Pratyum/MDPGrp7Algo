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

public class ExplorationSolver {
    private static Map objective_map = new Map(); // generated from map solver
    private static Map subjective_map = new Map();
    private Robot robot ;
    
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
		
        Vector2 robotPos = new Vector2(3, 2);
        Direction robotDir = Direction.Right;
        Robot robot = new Robot(robotPos, robotDir);

        SensingData s; 
        int x;
        
        // put some blockers into the map
        objective_map.addObstacle(_genBlockers(_map));
        
        simulator.initializeMap(objective_map);
        
        System.out.println(objective_map.toString(robot));
        
        
               
	    	
	    		
	    		
	    		//data = getDataFromRPI();
        for(x=0;x<10;x++){
            view(robot);
	    	
	    	    	if(mapViewer.checkWalkable(robot, Direction.Right)==1){
	    	    		robot.execute(RobotAction.RotateRight);
	    	    		robot.execute(RobotAction.MoveForward);
	    	    	}
	    	    	else if (mapViewer.checkWalkable(robot, Direction.Right)==0){
	    	    		turnLeftTillEmpty(robot); //now didnt turn left , so execute directly
	    	    	}
	    	    	else if (mapViewer.checkWalkable(robot, Direction.Right)==2){
	    	    		robot.execute(RobotAction.RotateRight);
	    	    		
	    	    		view(robot);
	    	    		
	
	    		    if (mapViewer.checkWalkable(robot, Direction.Up)==1){
	    		    		robot.execute(RobotAction.MoveForward);
	    		    	}
	    		    else if(mapViewer.checkWalkable(robot, Direction.Up)==0){
	    		    		robot.execute(RobotAction.RotateLeft);
	    		    		turnLeftTillEmpty(robot); 
	    		    }
	    		    	else
	    		    		System.out.println("Error1");
	    	    	}
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
    	
    		int check = mapViewer.checkWalkable(robot, Direction.Up);

    		if(check==2){
    			view(robot);
    		}
    		// make sure it is viewed before turn
    		//update
    		check = mapViewer.checkWalkable(robot, Direction.Up);
    		
    		if(check==1){
    			robot.execute(RobotAction.MoveForward);
    			return;
    		}
    		
    		
    		if(check==0){
    			robot.execute(RobotAction.RotateLeft);
    			turnLeftTillEmpty( robot);
    			
    		}

    	
    }
    

}
