package mdp.common;

public class Vector2 {

    private int _i;
    private int _j;

    public Vector2(int i, int j) {
        _i = i;
        _j = j;
    }

    public int i() {
        return _i;
    }

    public int j() {
        return _j;
    }

    public void i(int i) {
        _i = i;
    }

    public void j(int j) {
        _j = j;
    }

    @Override
    public String toString() {
        return "(" + _i + ", " + _j + ")";
    }

    public boolean equals(Vector2 coord) {
        return coord.i() == _i && coord.j() == _j;
    }

    public void add(Vector2 coord) {
        _i += coord.i();
        _j += coord.j();
    }

    public void multiply(int multiplier) {
        _i *= multiplier;
        _j *= multiplier;
    }

    public Vector2 fnAdd(Vector2 coord) {
        return new Vector2(_i + coord.i(), _j + coord.j());
    }

    public Vector2 fnMultiply(int multiplier) {
        return new Vector2(_i * multiplier, _j * multiplier);
    }
    
    @Override
    public boolean equals(Object obj) {
        return equals((Vector2) obj);
    }
}
