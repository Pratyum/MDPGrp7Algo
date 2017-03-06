package mdp.simulation.view;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
    
    private GridPanel _gridPanel;
    private DescControlPanel _descCtrlPanel;
    private RunControlPanel _runCtrlPanel;
    private InterruptControlPanel _intrCtrlPanel;
    private SimControlPanel _simCtrlPanel;

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

    public SimControlPanel getSimCtrlPanel() {
        return _simCtrlPanel;
    }

    public MainPanel() {
        // config
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // children
        _gridPanel = new GridPanel();
        _descCtrlPanel = new DescControlPanel();
        _runCtrlPanel = new RunControlPanel();
        _intrCtrlPanel = new InterruptControlPanel();
        _simCtrlPanel = new SimControlPanel();
        this.add(_gridPanel);
        this.add(_descCtrlPanel);
        this.add(_runCtrlPanel);
        this.add(_intrCtrlPanel);
        this.add(_simCtrlPanel);
    }
    
}
