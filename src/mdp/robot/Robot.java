package mdp.robot;

import java.io.IOException;
import java.util.LinkedList;

import mdp.Main;
import mdp.common.Direction;
import mdp.common.Vector2;
import mdp.map.Map;
import mdp.Main;
public class Robot {    
    private Vector2 _position;
    private Direction _orientation;
    private Direction _direction;
    private Map pathMap;
    private static volatile boolean actionCompleted =  false;
    private static LinkedList<RobotAction> bufferedActions= new LinkedList<>() ;
    
    public Robot() {
        this(new Vector2(1, 1), Direction.Right);
    }
    
    public Robot(Vector2 position, Direction direction) {
        _position = position;
        _orientation = direction;
    }
    
    public Vector2 position() { return _position; }
    public Direction orientation() { return _orientation; }
    public void position(Vector2 position) { _position = position; }
    public void orientation(Direction direction) { _orientation = direction; }
    public void execute(RobotAction action) {
        Vector2 dirVector = _orientation.toVector2();
        switch (action) {
            case MoveForward:
            		// RPI call
            		
                _position.add(dirVector);
                break;
            case MoveBackward:
            		// RPI call
                dirVector.multiply(-1);
                
                _position.add(dirVector);
                break;
            case RotateLeft:
            		// RPI call
                _orientation = _orientation.getLeft();
                break;
            case RotateRight:
            		// RPI call
                _orientation = _orientation.getRight();
                break;
        }
    }
    public boolean bufferAction(RobotAction action){
        return bufferedActions.add(action);
    }
    
    public static void actionCompletedCallBack(){
    		actionCompleted= true;
    		
    }
    public void executeBufferActions(int sleepPeriod) throws IOException {
        try {
        		if(Main.getSimulationMode() == false){
        			
        			Main.getRpi().sendMoveCommand(bufferedActions);
            		while(actionCompleted != true){}
            		
            		System.out.println("Actions completed");
            		actionCompleted = false;
        		}
        	
        		for (RobotAction action: bufferedActions) {
                execute(action);
                
                Main.getGUI().update(this);
                Thread.sleep(sleepPeriod);
            }
            bufferedActions.clear();
            
        } catch (InterruptedException e) {
            System.out.println("Robot execution interrupted");
        }
    }
    
    public boolean checkIfHavingBufferActions(){
        return !bufferedActions.isEmpty();
    }
}
