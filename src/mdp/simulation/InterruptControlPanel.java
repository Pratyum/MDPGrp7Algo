package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InterruptControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _stopBtn;
    private JButton _resetBtn;
    private JTextField _termCoverageText;
    private JTextField _termTimeText;
    
    public InterruptControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _stopBtn = new JButton("Stop");
        _resetBtn = new JButton("Reset");
        _termCoverageText = new JTextField("90", 3);
        _termTimeText = new JTextField("0", 3);
        JLabel termLabel = new JLabel("Terminate after: ");
        JLabel coverageLabel = new JLabel("% or");
        JLabel timeLabel = new JLabel("seconds");
        termLabel.setForeground(Color.WHITE);
        coverageLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        this.add(_stopBtn);
        this.add(_resetBtn);
        this.add(termLabel);
        this.add(_termCoverageText);
        this.add(coverageLabel);
        this.add(_termTimeText);
        this.add(timeLabel);
    }

    public JButton getStopBtn() {
        return _stopBtn;
    }

    public JButton getResetBtn() {
        return _resetBtn;
    }

    public JTextField getTermCoverageText() {
        return _termCoverageText;
    }

    public JTextField getTermTimeText() {
        return _termTimeText;
    }
    
}
