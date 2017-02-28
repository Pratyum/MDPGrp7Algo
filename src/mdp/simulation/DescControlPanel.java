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
    private JButton _getHexBtn;
    
    public DescControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _filePathBtn = new JTextField("descriptor.txt", 30);
        _openDescBtn = new JButton("Open");
        _saveDescBtn = new JButton("Save");
        _getHexBtn = new JButton("Get Hex");
        this.add(_filePathBtn);
        this.add(_openDescBtn);
        this.add(_saveDescBtn);
        this.add(_getHexBtn);
    }

    public JTextField getFilePathTextField() {
        return _filePathBtn;
    }

    public JButton getOpenDescBtn() {
        return _openDescBtn;
    }

    public JButton getSaveDescBtn() {
        return _saveDescBtn;
    }

    public JButton getGetHexBtn() {
        return _getHexBtn;
    }
    
}
