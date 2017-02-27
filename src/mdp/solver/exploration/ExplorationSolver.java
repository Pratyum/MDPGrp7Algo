package mdp.solver.exploration;
import java.util.ArrayList;



import java.util.List;

import com.apple.eawt.AppEvent.SystemSleepEvent;

import mdp.map.Waypoint;
import mdp.common.Direction;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.map.WPObstacleState;
import mdp.solver.exploration.MapViewer;
import mdp.robot.RobotAction;
import java.util.LinkedList;

public class ExplorationSolver {
    private static Map objective_map = new Map(); // generated from map solver
    private static Map subjective_map = new Map();
    private Robot robot ;

    private static Simulator simulator = new Simulator(objective_map);

    private static MapViewer mapViewer = new MapViewer();
    private static ActionFormulator actionFormulator = new ActionFormulator(mapViewer , simulator  );
    private static GoalFormulator goalFormulator = new GoalFormulator(mapViewer);
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
        simulator.addObstacle(_map);      
        System.out.println(objective_map.toString(robot));
        
	    		
	    		//data = getDataFromRPI();
        
        while(!goalFormulator.checkIfReachFinalGoal(robot.position())){

	    	    	actionFormulator.rightWallFollower(robot);
	    	    	// make a call to simulator
	    	    	
        }
        while(!goalFormulator.checkIfReachStartZone(robot.position())){

        		actionFormulator.rightWallFollower(robot);
	    	// make a call to simulator
	    	
        }
        		
	    	System.out.println("i:"+ robot.position().i());
	    	System.out.println("j:"+ robot.position().j());
	    	
	}

    
    
 
    

}
