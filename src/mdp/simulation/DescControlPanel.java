package mdp.simulation;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DescControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JTextField _filePathBtn;
    private JButton _openDescBtn;
    private JButton _saveDescBtn;
    
    public DescControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _filePathBtn = new JTextField("descriptor.txt", 40);
        _openDescBtn = new JButton("Open");
        _saveDescBtn = new JButton("Save");
        this.add(_filePathBtn);
        this.add(_openDescBtn);
        this.add(_saveDescBtn);
    }

    public JTextField getFilePathBtn() {
        return _filePathBtn;
    }

    public JButton getOpenDescBtn() {
        return _openDescBtn;
    }

    public JButton getSaveDescBtn() {
        return _saveDescBtn;
    }
    
}
