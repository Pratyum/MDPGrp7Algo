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


public class ExplorationSolver {
    private static Map objective_map = new Map(); // generated from map solver
    private static Map subjective_map = new Map();
    private Robot robot ;
    
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
        Vector2 edge,edge_l, edge_r;
        SensingData s= new SensingData(); 
        
        // put some blockers into the map
        objective_map.addObstacle(_genBlockers(_map));
        
        
        System.out.println(objective_map.toString(robot));

               
	    	
	    		
	    		
	    		//data = getDataFromRPI();
        		edge = robot.position().fnAdd(robot.direction().toVector2());
        		s.front_m = detect(edge , robot.direction());
        		edge_l = edge.fnAdd(robot.direction().getLeft().toVector2());
        		s.front_l = detect(edge_l , robot.direction());
        		edge_r = edge.fnAdd(robot.direction().getRight().toVector2());
        		s.front_r = detect(edge_r , robot.direction());
        		
        		s.left = detect(edge_l , robot.direction().getLeft());
        		s.right = detect(edge_r , robot.direction().getRight());
        		
        		
	    		subjective_map= mapViewer.updateMap(robot , s);
	    	
	    		System.out.println(subjective_map.toString(robot));
        		
	    	
	    	
		
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
    
    private static int detect(Vector2 position, Direction dir){
    		Vector2 tmp = new Vector2(position.i(),position.j());
    		Waypoint wp;
    		Vector2 unit;
    		unit = dir.toVector2();
    		
    		
    		    
	    		tmp.add(unit);
	    		wp = objective_map.getPoint(tmp);
	    		
	    		//seeing range is not relevant to virtual obstacle
    			if(!objective_map.checkValidPosition(wp.position()) || wp.obstacleState()==WPObstacleState.IsActualObstacle )
    				return 1;
    			tmp.add(unit);
    			wp = objective_map.getPoint(tmp);
    			if(!objective_map.checkValidPosition(wp.position()) || wp.obstacleState()==WPObstacleState.IsActualObstacle )
        			return 2;
    			tmp.add(unit);
    			wp = objective_map.getPoint(tmp);
    			if(!objective_map.checkValidPosition(wp.position()) || wp.obstacleState()==WPObstacleState.IsActualObstacle)
    				return 3;
    			return 0;
    			
    		}
    

}
