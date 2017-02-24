package mdp.simulation;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
    
    private GridPanel _gridPanel;
    private DescControlPanel _descCtrlPanel;
    private RunControlPanel _runCtrlPanel;
    private InterruptControlPanel _intrCtrlPanel;

    public GridPanel getGridPanel() {
        return _gridPanel;
    }

    public DescControlPanel getDescCtrlPanel() {
        return _descCtrlPanel;
    }

    public RunControlPanel getRunCtrlPanel() {
        return _runCtrlPanel;
    }

    public InterruptControlPanel getIntrCtrlPanel() {
        return _intrCtrlPanel;
    }

    public MainPanel() {
        // config
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // children
        _gridPanel = new GridPanel();
        _descCtrlPanel = new DescControlPanel();
        _runCtrlPanel = new RunControlPanel();
        _intrCtrlPanel = new InterruptControlPanel();
        this.add(_gridPanel);
        this.add(_descCtrlPanel);
        this.add(_runCtrlPanel);
        this.add(_intrCtrlPanel);
    }
    
}
