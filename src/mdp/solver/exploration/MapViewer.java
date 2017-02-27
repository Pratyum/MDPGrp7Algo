package mdp.solver.exploration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

import mdp.common.Console;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.map.Waypoint;
import mdp.common.Direction;
import mdp.Main;
public class MapViewer{
	
	private static Map map;
	private int detectRange;  //default to 3
    private float[] front_sensors ;
    private float left_sensor;
    private float right_sensor;
    public static final int DIM_I = 15;
    public static final int DIM_J = 20;
    
    
    //1 empty, 2 obstacle, 0 havent explored
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
    
    private static int checkExploredState(Vector2 v){
    		if(!map.checkValidBoundary(v))
    			return 2; 
    		return explored[v.i()][v.j()];
    		
    }
    
    public Map getSubjectiveMap(){
    		return map;
    }
    
    private static void convert
    
    public  String exploredAreaToString(){
        String result = "";
        for (int i = -1; i <= DIM_I; i++) {
            for (int j = -1; j <= DIM_J; j++) {
                if (i == -1 || j == -1 || i == DIM_I || j == DIM_J) {
                    result += "# ";
                } else {
                  if(explored[i][j]== 0)
                	  	result +="0 ";
                  else if (explored[i][j]== 2)
                	  	result +="x ";
                  else	
                  	result +="  ";
                }
            }
            result += "\n";
        }
        return result;
    	
    }
    
    
    public Know checkAllAroundEmpty(Robot robot) throws InterruptedException{
        if(robot.checkIfHavingBufferActions())
    			robot.executeBufferActions();
        
    		
    		Know l ,r , b ,f;
    		
    		l =checkWalkable(robot ,  Direction.Left);
    		r =checkWalkable(robot ,  Direction.Right);
    		b =checkWalkable(robot ,  Direction.Down);
    		f =checkWalkable(robot ,  Direction.Up);
    		
    		if(l == Know.Yes && r ==Know.Yes && b ==Know.Yes && f ==Know.Yes){
    			return Know.Yes;
    		}
    		if(l == Know.Unsure || r ==Know.Unsure || b ==Know.Unsure || f ==Know.Unsure){
    			return Know.Unsure;
    		}
    		else
    			return Know.No;
    		
    }
    
    
    
    // 1 walkable, 0 not walkable, 2 need further exploration
    public Know checkWalkable(Robot robot , Direction d) throws InterruptedException{
        if(robot.checkIfHavingBufferActions())
    			robot.executeBufferActions();
        
    		
    		Vector2 edge1, edge2, edge3;
    		int s1,s2,s3;
    		Direction dir = Direction.Up;
    		
    		switch(d){
    		case  Up:
    			dir = robot.orientation();break;
    		case Down: 
    			dir = robot.orientation().getLeft().getLeft();break;
    		case Right:
    			dir = robot.orientation().getRight();break;
    		case Left:
    			dir = robot.orientation().getLeft();break;
    		default:
    			break;
    		}
    		
    		
    		
    		edge2 = robot.position().fnAdd(dir.toVector2().fnMultiply(2));
    		edge1 = edge2.fnAdd(dir.getLeft().toVector2());
    		edge3 = edge2.fnAdd(dir.getRight().toVector2());
    		
    		s1 = checkExploredState(edge1);
    		s2 = checkExploredState(edge2);
    		s3 = checkExploredState(edge3);
    		
    		if(s1 == 1 && s2 == 1 && s3 == 1 )
    			return Know.Yes;
    		else if(s1 == 2 || s2 == 2 || s3 == 2)  // got obstacle
    			return Know.No;
    		else 
    			return Know.Unsure;
    		
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
		
		
		
		
		edge= robot.position().fnAdd(robot.orientation().toVector2());
		edge_l= edge.fnAdd(robot.orientation().getLeft().toVector2());
		edge_r= edge.fnAdd(robot.orientation().getRight().toVector2());
		
		if(s.front_m != 0){
			obstaclePosition = edge.fnAdd(robot.orientation().toVector2().fnMultiply(s.front_m));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<s.front_m; i++){
				markExploredEmpty(edge.fnAdd(robot.orientation().toVector2().fnMultiply(i)));	
			}
			markExploredObstacle(edge.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
			
		}
		else
		{
			for(i=1; i<=3; i++){
				markExploredEmpty(edge.fnAdd(robot.orientation().toVector2().fnMultiply(i)));	
			}
		}
		
		if(s.front_l != 0){
			obstaclePosition = edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(s.front_l));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<s.front_l; i++){
				markExploredEmpty(edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
			}
			markExploredObstacle(edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
		}
		else{
			for(i=1; i<=3; i++){
				markExploredEmpty(edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
			}
		}
		
		if(s.front_r != 0){
			obstaclePosition = edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(s.front_r));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i< s.front_r; i++){
				markExploredEmpty(edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
			}
			markExploredObstacle(edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
		}
		else{
			for(i=1; i<= 3; i++){
				markExploredEmpty(edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
			}
		}
		
		if(s.left != 0){
			obstaclePosition = edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(s.left));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<s.left; i++){
				markExploredEmpty(edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(i)));
			}
			markExploredObstacle(edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(i)));
		}
		else{
			for(i=1; i<=3; i++){
				markExploredEmpty(edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(i)));
			}
		}
		
		
		if(s.right != 0){
			obstaclePosition = edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(s.right));
			if(map.checkValidPosition(obstaclePosition))
				obstaclePositions.add(obstaclePosition);
			for(i=1; i<s.right; i++){
				markExploredEmpty(edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
			}
			markExploredObstacle(edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
		}
		
		else{
			for(i=1; i<=3; i++){
				markExploredEmpty(edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
			}
		}
		
		
		
		
		map.addObstacle(obstaclePositions);
		
		Main.getGUI().update(map);
		return map;
	}
	
	/*public void startSimulationTimer(){
		Timer timer = new Timer();
		//create an instance of an anonymous subclass
		timer.schedule( new TimerTask(){
			public void run(){
				System.out.println("I AM HERE");
				Main.getGUI().update(map);
				
			}
		}, 1000, 1000);
	
	}
	
	*/
	
}
