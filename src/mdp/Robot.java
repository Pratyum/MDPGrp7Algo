package mdp;

public class Robot {    
    private Vector2 _position;
    private Direction _orientation;
    
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
}
