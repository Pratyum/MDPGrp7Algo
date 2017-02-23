package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _explorationBtn = new JButton("Start Exploration");
    private JButton _shortestPathBtn = new JButton("Start Shortest Path");
    private JButton _combinedBtn = new JButton("Start Combined Algo");

    public JButton getExplorationBtn() {
        return _explorationBtn;
    }

    public JButton getShortestPathBtn() {
        return _shortestPathBtn;
    }

    public JButton getCombinedBtn() {
        return _combinedBtn;
    }
    
    public ControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _explorationBtn = new JButton("Start Exploration");
        _shortestPathBtn = new JButton("Start Shortest Path");
        _combinedBtn = new JButton("Start Combined Algo");
        this.add(_explorationBtn);
        this.add(_shortestPathBtn);
        this.add(_combinedBtn);
    }
    
    
    
}
