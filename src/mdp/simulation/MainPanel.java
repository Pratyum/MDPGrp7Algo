package mdp.simulation;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
    
    private GridPanel _gridPanel;
    private ControlPanel _controlPanel;

    public GridPanel getGridPanel() {
        return _gridPanel;
    }

    public ControlPanel getControlPanel() {
        return _controlPanel;
    }

    public MainPanel() {
        // config
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // children
        _gridPanel = new GridPanel();
        _controlPanel = new ControlPanel();
        this.add(_gridPanel);
        this.add(_controlPanel);
    }
    
}
