package mdp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

import mdp.communication.Translator;
import mdp.robot.Robot;
import mdp.communication.ITranslatable;
import mdp.simulation.GUI;
import mdp.simulation.event.GUIClickEvent;
import mdp.simulation.view.IGUIUpdatable;
import mdp.solver.exploration.ActionFormulator;
import mdp.solver.shortestpath.AStarSolver;

public class Main {

    private static IGUIUpdatable _gui;
    private static ITranslatable _rpi;

    public static void main(String[] args) throws IOException {

        AStarSolver solver = new AStarSolver();

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

    public static IGUIUpdatable getGUI() {
        return _gui;
    }

    public static ITranslatable getRpi() {
        return _rpi;
    }

    public static void restartGUI() {
        SwingUtilities.invokeLater(() -> {
            _gui = new GUI();
        });
    }

    private static void _listenToRPi() throws IOException {
        _rpi.listen(() -> {
            String inStr = _rpi.getInputBuffer();
            System.out.println("inStr = " + inStr);
            switch (inStr) {
                // Android start commands
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
                case "D":
                    Robot.actionCompletedCallBack();
                    break;
                default:
                    ActionFormulator.sensingDataCallback(inStr);
                    System.out.println("Unrecognized input");
                    break;
            }
        });
    }

}
