package mdp.solver.exploration;
import java.util.List;
import java.util.List;
import java.util.ArrayList;

import mdp.Console;
import mdp.Map;
import mdp.Robot;
import mdp.Vector2;
import mdp.Waypoint;

public class MapViewer{
	
	private static Map map;
	private int detectRange;  //default to 3
    private float[] front_sensors ;
    private float left_sensor;
    private float right_sensor;
    public static final int DIM_I = 15;
    public static final int DIM_J = 20;
    
    private static int[][] explored =       {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };
	
    MapViewer(){
    		map = new Map();
    }
    
    private static void markExploredEmpty(Vector2 v){
 
    		explored[v.i()][v.j()] = 1;
    }
    
    private static void markExploredObstacle(Vector2 v){
    	 
    		if(map.checkValidBoundary(v))  //prevent it being a wall , out of bound array
    			explored[v.i()][v.j()] = 2;
    }
    
    private static void markExploredDirectEmpty(int i, int j){
    	 
		explored[i][j] = 1;
    }
    
    
    
    public static String exploredAreaToString(){
        String result = "";
        for (int i = -1; i <= DIM_I; i++) {
            for (int j = -1; j <= DIM_J; j++) {
                if (i == -1 || j == -1 || i == DIM_I || j == DIM_J) {
                    result += "# ";
                } else {
                  if(explored[i][j]== 0)
                	  	result +="0 ";
                  else
                	  	result +="  ";
                }
            }
            result += "\n";
        }
        return result;
    	
    }
    
    
	//take RPI data from Solver , update what I saw
	public Map updateMap(Robot robot , SensingData s){
		List<Vector2> obstaclePositions = new ArrayList<>();
		Vector2 obstaclePosition;
		Vector2 edge, edge_l , edge_r;
		int i=1;
		Vector2 robotPosition = robot.position();
		markExploredEmpty(robotPosition);
		markExploredDirectEmpty(robotPosition.i(),robotPosition.j()+1);
		markExploredDirectEmpty(robotPosition.i(),robotPosition.j()-1);
		markExploredDirectEmpty(robotPosition.i()-1,robotPosition.j());
		markExploredDirectEmpty(robotPosition.i()+1,robotPosition.j());
		markExploredDirectEmpty(robotPosition.i()+1,robotPosition.j()+1);
		markExploredDirectEmpty(robotPosition.i()-1,robotPosition.j()-1);
		markExploredDirectEmpty(robotPosition.i()+1,robotPosition.j()-1);
		markExploredDirectEmpty(robotPosition.i()-1,robotPosition.j()+1);
		
		
		
		
		edge= robot.position().fnAdd(robot.direction().toVector2());
		edge_l= edge.fnAdd(robot.direction().getLeft().toVector2());
		edge_r= edge.fnAdd(robot.direction().getRight().toVector2());
		
		if(s.front_m != 0){
			obstaclePosition = edge.fnAdd(robot.direction().toVector2().fnMultiply(s.front_m));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<s.front_m; i++){
				markExploredEmpty(edge.fnAdd(robot.direction().toVector2().fnMultiply(i)));	
			}
			markExploredObstacle(edge.fnAdd(robot.direction().toVector2().fnMultiply(i)));
			
		}
		
		if(s.front_l != 0){
			obstaclePosition = edge_l.fnAdd(robot.direction().toVector2().fnMultiply(s.front_l));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<s.front_l; i++){
				markExploredEmpty(edge_l.fnAdd(robot.direction().toVector2().fnMultiply(i)));
			}
			markExploredObstacle(edge_l.fnAdd(robot.direction().toVector2().fnMultiply(i)));
		}
		
		if(s.front_r != 0){
			obstaclePosition = edge_r.fnAdd(robot.direction().toVector2().fnMultiply(s.front_r));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i< s.front_r; i++){
				markExploredEmpty(edge_r.fnAdd(robot.direction().toVector2().fnMultiply(i)));
			}
			markExploredObstacle(edge_r.fnAdd(robot.direction().toVector2().fnMultiply(i)));
		}
		
		if(s.left != 0){
			obstaclePosition = edge_l.fnAdd(robot.direction().getLeft().toVector2().fnMultiply(s.left));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<=s.left; i++){
				markExplored(edge_l.fnAdd(robot.direction().toVector2().fnMultiply(i)));
			}
		}
		
		
		if(s.right != 0){
			obstaclePosition = edge_r.fnAdd(robot.direction().getRight().toVector2().fnMultiply(s.right));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
		}
		
		map.addObstacle(obstaclePositions);
		
		return map;
	}
	
}
