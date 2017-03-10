package mdp.solver.exploration;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import mdp.map.Map;
import mdp.map.WPSpecialState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;
import mdp.common.Vector2;
import mdp.common.Direction;
import mdp.Main;
import mdp.map.WPObstacleState;


public class MapViewer {

    private Map map;

    //1 empty, 2 obstacle, 0 havent explored
    private int[][] explored;
    private int[][] robotPosition;
    public LinkedList<RobotMovementHistory> robotMovementHistory ;
    MapViewer() {
        map = new Map();
        explored = new int[][] {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        robotMovementHistory = new LinkedList<RobotMovementHistory>();
        robotPosition = new int[][] {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    public boolean markRobotVisited(Vector2 v){
    		if(map.checkValidPosition(v))
    			{
    				robotPosition[v.i()][v.j()] = 1;
    				return true;
    			}
    		return false;
    		
    }
    
    public boolean checkRobotVisited(Vector2 v){
		if(map.checkValidPosition(v))
			{
				if(robotPosition[v.i()][v.j()] ==1)
					return true;
			}
		return false;
		
    }
    
    
    public int[][] getExplored() {
        return explored;
    }

    public Map getSubjectiveMap() {
        return map;
    }

    public void markExploredEmpty(Vector2 v) {
        if(map.checkValidBoundary(v)) {
            explored[v.i()][v.j()] = 1;
        }
    }

    public void markUnreachable(Vector2 v) {
        if(map.checkValidBoundary(v)) {
            explored[v.i()][v.j()] = -1;
        }
    }
    	
    private void markExploredObstacle(Vector2 v) {

        if (map.checkValidBoundary(v)) //prevent it being a wall , out of bound array
        {
            explored[v.i()][v.j()] = 2;
        }
    }

    private void markExploredDirectEmpty(int i, int j) {

        explored[i][j] = 1;
    }

    private int checkExploredState(Vector2 v) {
        if (!map.checkValidBoundary(v)) {
            return 2;
        }
        return explored[v.i()][v.j()];

    }
    
    public void markRobotHistory(Vector2 p, Direction d){
    
    		robotMovementHistory.add(new RobotMovementHistory(p,d));	
    }
    
    public int detectCircle(Vector2 p, Direction d){
    		RobotMovementHistory tmp = new RobotMovementHistory(p,d);
    		for(int i=0; i<robotMovementHistory.size();i++){
    			if(RobotMovementHistory.compare(tmp,robotMovementHistory.get(i) )){
    				return i-1;
    			}
    		}
    		
    		return -1;
    	
    }
    
    public LinkedList<RobotMovementHistory> getRobotMovementHistory(){
    		return robotMovementHistory;
    }
    
    
    public boolean checkIfNavigationComplete(){
	    boolean complete= true;
	        for (int i = 0; i < Map.DIM_I; i++) {
	            for (int j = 0; j < Map.DIM_J; j++) {
	                if (explored[i][j] == 0) {
	                    complete = false;
	                    break;
	                }
	            }
	        }
	        
	   return complete;
    }
    
    private void insertExploredIntoMap() {
        LinkedList<Vector2> listOfObserved = new LinkedList<>();
        for (int i = 0; i < Map.DIM_I; i++) {
            for (int j = 0; j < Map.DIM_J; j++) {
                if (explored[i][j] > 0) {
                    listOfObserved.add(new Vector2(i, j));
                }
            }
        }
        map.highlight(listOfObserved, WPSpecialState.IsExplored);
    }

    public String exploredAreaToString() {
        String result = "";
        for (int i = -1; i <= Map.DIM_I; i++) {
            for (int j = -1; j <= Map.DIM_J; j++) {
                if (i == -1 || j == -1 || i == Map.DIM_I || j == Map.DIM_J) {
                    result += "# ";
                } else {
                    switch (explored[i][j]) {
                        case 0:
                            result += "0 ";
                            break;
                        case 2:
                            result += "x ";
                            break;
                        default:
                            result += "  ";
                            break;
                    }
                }
            }
            result += "\n";
        }
        return result;

    }

    public String robotVisitedPlaceToString() {
        String result = "";
        for (int i = -1; i <= Map.DIM_I; i++) {
            for (int j = -1; j <= Map.DIM_J; j++) {
                if (i == -1 || j == -1 || i == Map.DIM_I || j == Map.DIM_J) {
                    result += "# ";
                } else {
                    switch (robotPosition[i][j]) {
                        case 1:
                            result += ": ";
                            break;
                        
                        default:
                            result += "  ";
                            break;
                    }
                }
            }
            result += "\n";
        }
        return result;

    }
    
    public Know checkAllAroundEmpty(Robot robot) throws InterruptedException, IOException {
        if (robot.checkIfHavingBufferActions()) {
            robot.executeBufferActions(ExplorationSolver.getExePeriod());
        }

        Know l, r, b, f;

        l = checkWalkable(robot, Direction.Left);
        r = checkWalkable(robot, Direction.Right);
        b = checkWalkable(robot, Direction.Down);
        f = checkWalkable(robot, Direction.Up);

        if (l == Know.Yes && r == Know.Yes && b == Know.Yes && f == Know.Yes) {
            return Know.Yes;
        }
        if (l == Know.Unsure || r == Know.Unsure || b == Know.Unsure || f == Know.Unsure) {
            return Know.Unsure;
        } else {
            return Know.No;
        }

    }

    public boolean checkIfRight5SquaresEmpty(Robot robot) throws InterruptedException, IOException {
    	
    		Vector2 edge_up ,edge_mid , edge_down;
    	   
       return checkBackRightConnectingPoint(robot) != WPObstacleState.IsActualObstacle &&
        	checkFrontRightConnectingPoint(robot) != WPObstacleState.IsActualObstacle &&
        	checkWalkable(robot, Direction.Right) == Know.Yes; 
    	   		//do sth
    		

    }
    
    public void markRobotHistory(){
    	
    }
    
    // 1 walkable, 0 not walkable, 2 need further exploration
    public Know checkWalkable(Robot robot, Direction d) throws InterruptedException, IOException {
    	 	if (robot.checkIfHavingBufferActions()) {
             robot.executeBufferActions(ExplorationSolver.getExePeriod());
         }
    	 	
        Vector2 edge1, edge2, edge3;
        int s1, s2, s3;
        Direction dir = Direction.Up;

        switch (d) {
            case Up:
                dir = robot.orientation();
                break;
            case Down:
                dir = robot.orientation().getLeft().getLeft();
                break;
            case Right:
                dir = robot.orientation().getRight();
                break;
            case Left:
                dir = robot.orientation().getLeft();
                break;
            default:
                break;
        }

        edge2 = robot.position().fnAdd(dir.toVector2().fnMultiply(2));
        edge1 = edge2.fnAdd(dir.getLeft().toVector2());
        edge3 = edge2.fnAdd(dir.getRight().toVector2());

        s1 = checkExploredState(edge1);
        s2 = checkExploredState(edge2);
        s3 = checkExploredState(edge3);

        if (s1 == 1 && s2 == 1 && s3 == 1) {
            return Know.Yes;
        } else if (s1 == 2 || s2 == 2 || s3 == 2) // got obstacle
        {
            return Know.No;
        } else {
            return Know.Unsure;
        }

    }
    //take RPI data from Solver , update what I saw
    public Map updateMap(Robot robot, SensingData s) {
        List<Vector2> obstaclePositions = new ArrayList<>();
        Vector2 obstaclePosition;
        Vector2 edge, edge_l, edge_r , edge_b;
        int i = 1;
        Vector2 robotPosition = robot.position();
        markExploredEmpty(robotPosition);
        markExploredDirectEmpty(robotPosition.i(), robotPosition.j() + 1);
        markExploredDirectEmpty(robotPosition.i(), robotPosition.j() - 1);
        markExploredDirectEmpty(robotPosition.i() - 1, robotPosition.j());
        markExploredDirectEmpty(robotPosition.i() + 1, robotPosition.j());
        markExploredDirectEmpty(robotPosition.i() + 1, robotPosition.j() + 1);
        markExploredDirectEmpty(robotPosition.i() - 1, robotPosition.j() - 1);
        markExploredDirectEmpty(robotPosition.i() + 1, robotPosition.j() - 1);
        markExploredDirectEmpty(robotPosition.i() - 1, robotPosition.j() + 1);

        edge = robot.position().fnAdd(robot.orientation().toVector2());
        edge_l = edge.fnAdd(robot.orientation().getLeft().toVector2());
        edge_r = edge.fnAdd(robot.orientation().getRight().toVector2());
        edge_b = robot.position().fnAdd(robot.orientation().getRight().toVector2()).fnAdd(robot.orientation().getBehind().toVector2());
        if (s.front_m != 0) {
            obstaclePosition = edge.fnAdd(robot.orientation().toVector2().fnMultiply(s.front_m));
            if (map.checkValidBoundary(obstaclePosition)) {
                obstaclePositions.add(obstaclePosition);
                markExploredObstacle(obstaclePosition);
            }
            for (i = 1; i < s.front_m; i++) {
                markExploredEmpty(edge.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
            }
            

        } else {
            for (i = 1; i <= 2; i++) {
                markExploredEmpty(edge.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
            }
        }

        if (s.front_l != 0) {
            obstaclePosition = edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(s.front_l));
            if (map.checkValidBoundary(obstaclePosition)) {
                obstaclePositions.add(obstaclePosition);
                markExploredObstacle(obstaclePosition);
            }
            for (i = 1; i < s.front_l; i++) {
                markExploredEmpty(edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
            }
            markExploredObstacle(edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
        } else {
            for (i = 1; i <= 2; i++) {
                markExploredEmpty(edge_l.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
            }
        }

        if (s.front_r != 0) {
            obstaclePosition = edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(s.front_r));
            if (map.checkValidBoundary(obstaclePosition)) {
                obstaclePositions.add(obstaclePosition);
                markExploredObstacle(obstaclePosition);
            }
            for (i = 1; i < s.front_r; i++) {
                markExploredEmpty(edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
            }
            
        } else {
            for (i = 1; i <= 2; i++) {
                markExploredEmpty(edge_r.fnAdd(robot.orientation().toVector2().fnMultiply(i)));
            }
        }

        if (s.left != 0) {
            obstaclePosition = edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(s.left));
            if (map.checkValidBoundary(obstaclePosition)) {
                obstaclePositions.add(obstaclePosition);
                markExploredObstacle(obstaclePosition);
            }
            for (i = 1; i < s.left; i++) {
                markExploredEmpty(edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(i)));
            }
            
        } else {
            for (i = 1; i <= 3; i++) {
                markExploredEmpty(edge_l.fnAdd(robot.orientation().getLeft().toVector2().fnMultiply(i)));
            }
        }

        if (s.right_f != 0) {
            obstaclePosition = edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(s.right_f));
            if (map.checkValidBoundary(obstaclePosition)) {
                obstaclePositions.add(obstaclePosition);
                markExploredObstacle(obstaclePosition);
            
            }
            for (i = 1; i < s.right_f; i++) {
                markExploredEmpty(edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
            }
            
        } else {
            for (i = 1; i <= 2; i++) {
                markExploredEmpty(edge_r.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
            }
        }
        
        if (s.right_b != 0) {
        		
            obstaclePosition = edge_b.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(s.right_b));
            if (map.checkValidBoundary(obstaclePosition)) {
                obstaclePositions.add(obstaclePosition);
                markExploredObstacle(obstaclePosition);
            
            }
            for (i = 1; i < s.right_b; i++) {
                markExploredEmpty(edge_b.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
            }
            
        } else {
            for (i = 1; i <= 2; i++) {
                markExploredEmpty(edge_b.fnAdd(robot.orientation().getRight().toVector2().fnMultiply(i)));
            }
        }

        //update map with proper obstacles
        map = new Map(explored, true);
        map.addObstacle(obstaclePositions);
        insertExploredIntoMap();
        Main.getGUI().update(map);
        return map;
    }

    public LinkedList<Vector2> findUnexploredInAscendingDistanceOrder(Robot robot){
    		int i = 1;
    		LinkedList<Vector2> total = new LinkedList<Vector2>();
    		
    		do{
    			 total.addAll(IdentifyUnexploredAround(i,robot.position()));
    			 i++;
    			 
    		}while(i !=20);
    		return total;
    }
    
    private  List<Vector2> IdentifyUnexploredAround(int width, Vector2 center ){
    		Vector2 traverse = center.fnAdd(new Vector2(width, -width));
    		List<Vector2> list = new ArrayList<>();
    		
    		int i;
    		for(i=0; i<width*2;i++){
    			if(checkValidExploredRange(traverse)){
    				
    				if(explored[traverse.i()][traverse.j()]==0 )
    					{
    					list.add( new Vector2(traverse.i(), traverse.j()));	
    					
    					}	
    			}
    			traverse.add(new Vector2(0,1));
    		}
    		
    		for(i=0; i<width*2;i++){
    			if(checkValidExploredRange(traverse)){
    				if(explored[traverse.i()][traverse.j()]==0 )
    					{
    					list.add( new Vector2(traverse.i(), traverse.j()));	
    					
    					}
    			}
    			traverse.add(new Vector2(-1,0));
    		}
    		
    		for(i=0; i<width*2;i++){
    			if(checkValidExploredRange(traverse)){
    				if(explored[traverse.i()][traverse.j()]==0)
    					{
    					list.add( new Vector2(traverse.i(), traverse.j()));	
    					
    					}
    			}
    			traverse.add(new Vector2(0,-1));
    		}
    		
    		for(i=0; i<width*2;i++){
    			if(checkValidExploredRange(traverse)){
    				if(explored[traverse.i()][traverse.j()]==0)
    				{
    					list.add( new Vector2(traverse.i(), traverse.j()));	
    					
    					}
    			}
    			traverse.add(new Vector2(1,0));
    		}
    		
    		return list;
    		 // the special vector marks no vector found
    		
    }
    
    public boolean markGhostBlock(Vector2 center){
    		Vector2 up, down, right, left;
    		boolean upBlocked = false;
    		boolean downBlocked = false;
    		boolean rightBlocked = false;
    		boolean leftBlocked = false;
    		up = center.fnAdd(new Vector2(-1,0));
    		down = center.fnAdd(new Vector2(1,0));
    		right = center.fnAdd(new Vector2(0,1));
    		left = center.fnAdd(new Vector2(0,-1));
    		int i;
    		
    		for(i=0; i< 3; i++){
    			if(map.checkValidBoundary(up) && map.getPoint(up).obstacleState() == WPObstacleState.IsActualObstacle){
    				upBlocked = true;
    				break;
    			}
    			up.fnAdd(new Vector2(-1,0));
    		}
    		
    		for(i=0; i< 3; i++){
    			if(map.checkValidBoundary(down) && map.getPoint(down).obstacleState() == WPObstacleState.IsActualObstacle){
    				downBlocked = true;
    				break;
    			}
    			down.fnAdd(new Vector2(1,0));
    		}
    		
    		for(i=0; i< 3; i++){
    			if(map.checkValidBoundary(right) && map.getPoint(right).obstacleState() == WPObstacleState.IsActualObstacle){
    				rightBlocked = true;
    				break;
    			}
    			right.fnAdd(new Vector2(0,1));
    		}
    		
    		for(i=0; i< 3; i++){
    			if(map.checkValidBoundary(left) && map.getPoint(left).obstacleState() == WPObstacleState.IsActualObstacle){
    				leftBlocked = true;
    				break;
    			}
    			left.fnAdd(new Vector2(0,-1));
    		}
    		
    		if(upBlocked == true && downBlocked == true && leftBlocked == true && rightBlocked == true){
    			markUnreachable(center);
    			return true;
    		}
    		else
    			return false;
    	
    }
    
    
    private boolean checkValidExploredRange(Vector2 v){
    		
    		return v.i() >=0  && v.i() < (map.DIM_I) &&  v.j() >=0  && v.j() < (map.DIM_J);
    }
    
    public boolean validate(Robot robot, RobotAction action) throws InterruptedException, IOException{
    		Vector2 position;
    		switch(action){
    		case MoveForward:  
    				if(checkWalkable(robot, Direction.Up)==Know.No){
    					return false;
    				}
    				break;
    			default: return true;
    		}
    		
    		return true;
    }
    
    public LinkedList<Vector2> findScannableReachableFromGoal(Vector2 position, Robot robot){
    		
    		LinkedList<Vector2> reachable = new LinkedList<Vector2>();
    		AStarSolver astarSolver = new AStarSolver();
    		AStarSolverResult astarSolverResult ;
    		
    		
    		if(map.checkValidBoundary(position) && map.getPoint(position).obstacleState() == WPObstacleState.IsWalkable)
    			{
    				astarSolverResult = astarSolver.solve(getSubjectiveMap(), robot, position);
    				
    				if (!astarSolverResult.shortestPath.isEmpty())
    				{
    					reachable.add(position);
    				}
    			}
    	
    		int i = 1;
		LinkedList<Vector2> list;
		int num;
			while(i<=3){
				 list = IdentifyWalkableAround(i,position);
				 num = 0;
				 
				 for(num = 0; num < list.size();num++){
					 astarSolverResult = astarSolver.solve(getSubjectiveMap(), robot, list.get(num));
					 if (!astarSolverResult.shortestPath.isEmpty())
						 reachable.add(new Vector2(list.get(num).i(), list.get(num).j()));
					 
				 } 
				 list.clear();
				 i++;
			};
			
		return reachable;
    		
    }
    
    private LinkedList<Vector2> IdentifyWalkableAround(int width, Vector2 center ){
		Vector2 up = center.fnAdd(new Vector2(-width, 0));
		Vector2 down = center.fnAdd(new Vector2(width, 0));
		Vector2 left = center.fnAdd(new Vector2(0, -width));
		Vector2 right = center.fnAdd(new Vector2(0, width));
		
		LinkedList<Vector2> traversingList = new LinkedList<Vector2>();
		
		
		addWalkableToList(traversingList, up);
		addWalkableToList(traversingList, up.fnAdd(new Vector2(0,1)));
		addWalkableToList(traversingList, up.fnAdd(new Vector2(0,-1)));
		addWalkableToList(traversingList, down);
		addWalkableToList(traversingList, down.fnAdd(new Vector2(0,1)));
		addWalkableToList(traversingList, down.fnAdd(new Vector2(0,-1)));
		addWalkableToList(traversingList, left);
		addWalkableToList(traversingList, left.fnAdd(new Vector2(-1,0)));
		addWalkableToList(traversingList, left.fnAdd(new Vector2(1,0)));
		addWalkableToList(traversingList, right);
		addWalkableToList(traversingList, right.fnAdd(new Vector2(1,0)));
		addWalkableToList(traversingList, right.fnAdd(new Vector2(-1,0)));
		
		
		//continue
		
		
		return traversingList; // the special vector marks no vector found
		
}
    
    private void addWalkableToList(LinkedList<Vector2> traversingList, Vector2 v){
    		if(checkValidExploredRange(v)){
			if( map.getPoint(v).obstacleState() == WPObstacleState.IsWalkable )
				traversingList.add(v);		
		}
    }
    
    
    public ArrayList<Vector2> getUnExplored(){
        ArrayList<Vector2> unexplored = new ArrayList<Vector2>() ;
        for(int row=0;row<Map.DIM_I;++row){
            for(int col=0;col<Map.DIM_J;++col){
                if(explored[row][col] == 0){
                    if(unexplored.size()>0 && unexplored.get(unexplored.size()-1).j()==row){
                        unexplored.remove(unexplored.size()-1);
                    }
                    Vector2 pair = new Vector2(row, col);
                    System.out.println(pair);
                    unexplored.add(pair);
                    
                }
            }
        }
        int count=0;
        while(unexplored.size()-2 >= count){
            if(unexplored.get(count+1).i() ==unexplored.get(count).i()){
                if(unexplored.get(count+1).j() - unexplored.get(count).j() <= 2){
                    unexplored.remove(count);
                    --count;
                }
            }
            ++count;
        }
        
        return unexplored;
    }
    
    
    public CalibrationType checkCalibrationAvailable(Robot robot){
        Vector2 front_l, front_r, front_m, right_up,right_down;
        
        front_m = robot.position().fnAdd(robot.orientation().toVector2().fnMultiply(2));
        front_l = front_m.fnAdd(robot.orientation().getLeft().toVector2());
        front_r = front_m.fnAdd(robot.orientation().getRight().toVector2());
    		
        right_up = robot.position().fnAdd(robot.orientation().toVector2()).fnAdd(robot.orientation().getRight().toVector2().fnMultiply(2));
        right_down = robot.position().fnAdd(robot.orientation().getBehind().toVector2()).fnAdd(robot.orientation().getRight().toVector2().fnMultiply(2));
    
    
        /*if(map.getPoint(right_up).obstacleState() == WPObstacleState.IsActualObstacle && 
        		map.getPoint(right_down).obstacleState() == WPObstacleState.IsActualObstacle){
        		return CalibrationType.Right;
        }*/
        
        
	        if(!map.checkValidBoundary(front_r) || map.getPoint(front_r).obstacleState() == WPObstacleState.IsActualObstacle)
	        		if(!map.checkValidBoundary(front_l) ||map.getPoint(front_l).obstacleState() == WPObstacleState.IsActualObstacle){
	        		return CalibrationType.Front_LR;
	        }
        
	        if(!map.checkValidBoundary(front_m) || map.getPoint(front_m).obstacleState() == WPObstacleState.IsActualObstacle)
        		if(!map.checkValidBoundary(front_l) ||map.getPoint(front_l).obstacleState() == WPObstacleState.IsActualObstacle){
        		return CalibrationType.Front_ML;
        		}
	        
	        if(!map.checkValidBoundary(front_r) || map.getPoint(front_r).obstacleState() == WPObstacleState.IsActualObstacle)
	        		if(!map.checkValidBoundary(front_m) ||map.getPoint(front_m).obstacleState() == WPObstacleState.IsActualObstacle){
	        		return CalibrationType.Front_MR;
        		}
	       
        
        return CalibrationType.NA;
        
    }
    
    public WPObstacleState checkFrontRightConnectingPoint(Robot robot){
    		
    		if(!map.checkValidBoundary(robot.position().fnAdd(robot.orientation().toVector2().fnMultiply(2)).fnAdd(robot.orientation().getRight().toVector2().fnMultiply(2))))
    			return WPObstacleState.IsActualObstacle;
    					
    		return map.getPoint(robot.position().fnAdd(robot.orientation().toVector2().fnMultiply(2)).fnAdd(robot.orientation().getRight().toVector2().fnMultiply(2))).obstacleState();  
    }
    
    public WPObstacleState checkBackRightConnectingPoint(Robot robot){
    		if(!map.checkValidBoundary(robot.position().fnAdd(robot.orientation().toVector2().fnMultiply(-2)).fnAdd(robot.orientation().getRight().toVector2().fnMultiply(2))))
			return WPObstacleState.IsActualObstacle;
    		
		return map.getPoint(robot.position().fnAdd(robot.orientation().toVector2().fnMultiply(-2)).fnAdd(robot.orientation().getRight().toVector2().fnMultiply(2))).obstacleState();  
    }
    
    
}



