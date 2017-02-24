package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _loadDescBtn;
    private JButton _explorationBtn;
    private JButton _shortestPathBtn;
    private JButton _combinedBtn;
    private JButton _stopBtn;
    private JButton _resetBtn;
    
    public ControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _loadDescBtn = new JButton("Load Map from Descriptor");
        _explorationBtn = new JButton("Exploration");
        _shortestPathBtn = new JButton("Shortest Path");
        _combinedBtn = new JButton("Combined Algo");
        _stopBtn = new JButton("Stop");
        _resetBtn = new JButton("Reset");
        this.add(_loadDescBtn);
        this.add(_explorationBtn);
        this.add(_shortestPathBtn);
        this.add(_combinedBtn);
        this.add(_stopBtn);
        this.add(_resetBtn);
    }

    public JButton getLoadDescBtn() {
        return _loadDescBtn;
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
