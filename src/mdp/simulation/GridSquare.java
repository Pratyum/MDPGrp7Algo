package mdp.simulation;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mdp.common.Vector2;

public class GridSquare extends JPanel {
    
    private Vector2 _position;

    public GridSquare(Vector2 position) {
        _position = position;
        JLabel label = new JLabel(_position.toString());
        label.setForeground(Color.white);
        label.setFont(new Font(Font.SERIF, Font.PLAIN, 11));
        this.add(label); 
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
