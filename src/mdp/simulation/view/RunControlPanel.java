package mdp.simulation.view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RunControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _explorationBtn;
    private JButton _shortestPathBtn;
    private JButton _combinedBtn;
    private JTextField _exePeriod;
    
    public RunControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _explorationBtn = new JButton("Exploration");
        _shortestPathBtn = new JButton("Shortest Path");
        _combinedBtn = new JButton("Combined");
        _exePeriod = new JTextField("100", 5);
        this.add(_explorationBtn);
        this.add(_shortestPathBtn);
        this.add(_combinedBtn);
        this.add(_exePeriod);
        JLabel exePeriodLabel = new JLabel("s/action");
        exePeriodLabel.setForeground(Color.WHITE);
        this.add(exePeriodLabel);
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

    public JTextField getExePeriod() {
        return _exePeriod;
    }
    
}
