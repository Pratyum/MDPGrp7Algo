package mdp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

import mdp.communication.Translator;
import mdp.communication.ITranslatable;
import mdp.robot.RobotAction;
import mdp.simulation.event.EventHandler;
import mdp.simulation.GUI;
import mdp.simulation.event.GUIClickEvent;
import mdp.simulation.view.IGUIUpdatable;

public class Main {
    
    public static IGUIUpdatable _gui;
    public static ITranslatable _rpi;
    
    public static void main(String[] args) throws IOException {
                
        // run simulation
        System.out.println("Initiating GUI...");
        restartGUI();
        _rpi = new Translator();
        _rpi.connect(() -> {
            try {
                _listenToRPi();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // connect & send string to RPi
//        List<RobotAction> test = new ArrayList<>();
//        test.add(RobotAction.RotateLeft);
//        test.add(RobotAction.MoveForward);
//        test.add(RobotAction.MoveForward);
//        test.add(RobotAction.RotateRight);
//        test.add(RobotAction.MoveForward);
//        _rpi.sendToArduino(test); // lffrf

    }
    
    public static void restartGUI() {
        SwingUtilities.invokeLater(() -> {
            _gui = new GUI();
        });
    }
    
    public static IGUIUpdatable getGUI() {
        return _gui;
    }
    
    private static void _listenToRPi() throws IOException {
        _rpi.listen(() -> {
            String inStr = _rpi.getInputBuffer();
            System.out.println("inStr = " + inStr);
            switch (inStr) {
                case "e":
                    System.out.println("Triggering Exploration");
                    _gui.trigger(GUIClickEvent.OnExploration);
                    break;
                case "s":
                    System.out.println("Triggering ShortestPath");
                    _gui.trigger(GUIClickEvent.OnShortestPath);
                    break;
                case "c":
                    System.out.println("Triggering Combined");
                    _gui.trigger(GUIClickEvent.OnCombined);
                    break;
                default:
                    System.out.println("Unrecognized input");
                    break;
            }
        });
    }
    
}
