package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;

public class InterruptControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _stopBtn;
    private JButton _resetBtn;
    
    public InterruptControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _stopBtn = new JButton("Stop");
        _resetBtn = new JButton("Reset");
        this.add(_stopBtn);
        this.add(_resetBtn);
    }

    public JButton getStopBtn() {
        return _stopBtn;
    }

    public JButton getResetBtn() {
        return _resetBtn;
    }
    
}
