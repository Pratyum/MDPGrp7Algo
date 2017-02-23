package mdp;

public class Robot {    
    private Vector2 _position;
    private Direction _direction;
    
    public Robot(Vector2 position, Direction direction) {
        _position = position;
        _direction = direction;
    }
    
    public Vector2 position() { return _position; }
    public Direction direction() { return _direction; }
    public void position(Vector2 position) { _position = position; }
    public void direction(Direction direction) { _direction = direction; }
    
    
    
    public void execute(RobotAction action) {
        Vector2 dirVector = _direction.toVector2();
        switch (action) {
            case MoveForward:
                _position.add(dirVector);
                break;
            case MoveBackward:
                dirVector.multiply(-1);
                _position.add(dirVector);
                break;
            case RotateLeft:
                _direction = _direction.getLeft();
                break;
            case RotateRight:
                _direction = _direction.getRight();
                break;
        }
    }
}
