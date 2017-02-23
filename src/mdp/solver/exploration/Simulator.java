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
		
		
		edge = robot.position().fnAdd(robot.direction().toVector2());
		s.front_m = detect(edge , robot.direction());
		edge_l = edge.fnAdd(robot.direction().getLeft().toVector2());
		s.front_l = detect(edge_l , robot.direction());
		edge_r = edge.fnAdd(robot.direction().getRight().toVector2());
		s.front_r = detect(edge_r , robot.direction());
		
		s.left = detect(edge_l , robot.direction().getLeft());
		s.right = detect(edge_r , robot.direction().getRight());
		
		return s;
		
	}
	
	private static int detect(Vector2 position, Direction dir){
		Vector2 tmp = new Vector2(position.i(),position.j());
		Waypoint wp;
		Vector2 unit;
		unit = dir.toVector2();
		
		
		    
    		tmp.add(unit);
    		wp = objective_map.getPoint(tmp);
    		
    		//seeing range is not relevant to virtual obstacle
			if(!objective_map.checkValidBoundary(wp.position()) || wp.obstacleState()==WPObstacleState.IsActualObstacle )
				return 1;
			tmp.add(unit);
			wp = objective_map.getPoint(tmp);
			if(!objective_map.checkValidBoundary(wp.position()) || wp.obstacleState()==WPObstacleState.IsActualObstacle )
    			return 2;
			tmp.add(unit);
			wp = objective_map.getPoint(tmp);
			if(!objective_map.checkValidBoundary(wp.position()) || wp.obstacleState()==WPObstacleState.IsActualObstacle)
				return 3;
			return 0;
			
		}

	
	
	
}

