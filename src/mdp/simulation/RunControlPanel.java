package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;

public class RunControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _explorationBtn;
    private JButton _shortestPathBtn;
    private JButton _combinedBtn;
    
    public RunControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _explorationBtn = new JButton("Exploration");
        _shortestPathBtn = new JButton("Shortest Path");
        _combinedBtn = new JButton("Combined");
        this.add(_explorationBtn);
        this.add(_shortestPathBtn);
        this.add(_combinedBtn);
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
    
}
