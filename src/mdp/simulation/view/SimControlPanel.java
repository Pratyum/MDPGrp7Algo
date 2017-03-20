package mdp.simulation.view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import mdp.Main;

public class SimControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JCheckBox _simCheckBox;
    private JButton _connectBtn;

    public SimControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _simCheckBox = new JCheckBox("Simulation", Main.isSimulating());
        _connectBtn = new JButton("Connect to RPi");
        this.add(_simCheckBox);
        this.add(_connectBtn);
    }

    public JCheckBox getSimCheckBox() {
        return _simCheckBox;
    }

    public JButton getConnectBtn() {
        return _connectBtn;
    }
    
    
}
