package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _explorationBtn;
    private JButton _shortestPathBtn;
    private JButton _combinedBtn;
    private JButton _stopBtn;
    private JButton _resetBtn;
    
    public ControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _explorationBtn = new JButton("Start Exploration");
        _shortestPathBtn = new JButton("Start Shortest Path");
        _combinedBtn = new JButton("Start Combined Algo");
        _stopBtn = new JButton("Stop");
        _resetBtn = new JButton("Reset");
        this.add(_explorationBtn);
        this.add(_shortestPathBtn);
        this.add(_combinedBtn);
        this.add(_stopBtn);
        this.add(_resetBtn);
    }

    public JButton getExplorationBtn() {
        return _explorationBtn;
    }

    public JButton getShortestPathBtn() {
        return _shortestPathBtn;
    }

    public JButton getCombinedBtn() {
        return _combinedBtn;
    }

    public JButton getStopBtn() {
        return _stopBtn;
    }

    public JButton getResetBtn() {
        return _resetBtn;
    }
    
}
