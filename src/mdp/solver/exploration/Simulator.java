package mdp.solver.exploration;

import mdp.Direction;
import mdp.Map;
import mdp.Robot;
import mdp.Vector2;
import mdp.WPObstacleState;
import mdp.Waypoint;
public class Simulator {

	private static Map objective_map;
	private static SensingData s = new SensingData();
	public void initializeMap(Map map){
		objective_map = map;
		 
	}
	
	
	
	public SensingData getSensingData(Robot robot){
		Vector2 edge, edge_l , edge_r;
		
		
		edge = robot.position().fnAdd(robot.orientation().toVector2());
		s.front_m = detect(edge , robot.orientation());
		System.out.println("s.front_m " +s.front_m);
		edge_l = edge.fnAdd(robot.orientation().getLeft().toVector2());
		s.front_l = detect(edge_l , robot.orientation());
		System.out.println("s.front_l" +s.front_l);
		edge_r = edge.fnAdd(robot.orientation().getRight().toVector2());
		s.front_r = detect(edge_r , robot.orientation());
		System.out.println("s.front_r " +s.front_r);
		
		s.left = detect(edge_l , robot.orientation().getLeft());
		System.out.println("s.left " +s.left);
		s.right = detect(edge_r , robot.orientation().getRight());
		System.out.println("s.right " +s.right);
		return s;
		
	}
	
	private static int detect(Vector2 position, Direction dir){
		Vector2 tmp = new Vector2(position.i(),position.j());
		Waypoint wp;
		Vector2 unit;
		unit = dir.toVector2();
		
		
		    
    		tmp.add(unit);
  
    		
    		//seeing range is not relevant to virtual obstacle
			if(!objective_map.checkValidBoundary(tmp) || objective_map.getPoint(tmp).obstacleState()==WPObstacleState.IsActualObstacle )
				return 1; 
			tmp.add(unit);

			if(!objective_map.checkValidBoundary(tmp) || objective_map.getPoint(tmp).obstacleState()==WPObstacleState.IsActualObstacle )
    			return 2;
			tmp.add(unit);

			if(!objective_map.checkValidBoundary(tmp) || objective_map.getPoint(tmp).obstacleState()==WPObstacleState.IsActualObstacle)
				return 3;
			return 0; // no obstacle in front
			
		}

	
	
	
}
