package mdp.solver.exploration;
import java.util.List;
import java.util.List;
import java.util.ArrayList;

import mdp.Map;
import mdp.Robot;
import mdp.Vector2;

public class MapViewer{
	
	private Map map;
	private int detectRange;  //default to 3
    private float[] front_sensors ;
    private float left_sensor;
    private float right_sensor;
	
    MapViewer(){
    		map = new Map();
    }
    

	//take RPI data from Solver , update what I saw
	public Map updateMap(Robot robot , SensingData s){
		List<Vector2> obstaclePositions = new ArrayList<>();
		Vector2 obstaclePosition;
		Vector2 edge, edge_l , edge_r;
		
		edge= robot.position().fnAdd(robot.direction().toVector2());
		edge_l= edge.fnAdd(robot.direction().getLeft().toVector2());
		edge_r= edge.fnAdd(robot.direction().getRight().toVector2());
		if(s.front_m != 0){
			obstaclePosition = edge.fnAdd(robot.direction().toVector2().fnMultiply(s.front_m));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
		}
		
		if(s.front_l != 0){
			obstaclePosition = edge_l.fnAdd(robot.direction().toVector2().fnMultiply(s.front_l));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
		}
		
		if(s.front_r != 0){
			obstaclePosition = edge_r.fnAdd(robot.direction().toVector2().fnMultiply(s.front_r));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
		}
		
		if(s.left != 0){
			obstaclePosition = edge_l.fnAdd(robot.direction().getLeft().toVector2().fnMultiply(s.left));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
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
