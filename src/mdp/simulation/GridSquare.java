package mdp.simulation;

import javax.swing.JPanel;
import mdp.common.Vector2;

public class GridSquare extends JPanel {
    
    private Vector2 _position;

    public GridSquare(Vector2 position) {
        _position = position;
    }

    public Vector2 position() {
        return _position;
    }
    
    public void toggleBackground() {
        if (this.getBackground().equals(ColorConfig.OBSTACLE)) {
            this.setBackground(ColorConfig.NORMAL);
        } else {
            this.setBackground(ColorConfig.OBSTACLE);
        }
    }
    
}
