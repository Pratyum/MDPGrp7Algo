package mdp.robot;

import java.util.LinkedList;

import mdp.common.Direction;
import mdp.common.Vector2;
import mdp.map.Map;

public class Robot {    
    private Vector2 _position;
    private Direction _orientation;
    private Direction _direction;
    private Map pathMap;
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
                _position.add(dirVector);
                break;
            case MoveBackward:
                dirVector.multiply(-1);
                _position.add(dirVector);
                break;
            case RotateLeft:
                _orientation = _orientation.getLeft();
                break;
            case RotateRight:
                _orientation = _orientation.getRight();
                break;
        }
    }
    public boolean bufferAction(RobotAction action){
    		return bufferedActions.add(action);
    }
    public void executeBufferActions(){
    		RobotAction robotAction;
    		for(int i = 0 ; i <bufferedActions.size() ; i++){
    			robotAction = bufferedActions.get(i);
    			execute(robotAction);
    		}
    		bufferedActions.clear();
    }
    
    public boolean checkIfHavingBufferActions(){
    		return !bufferedActions.isEmpty();
    }
}
