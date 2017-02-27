package mdp;

import mdp.robot.RobotAction;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import mdp.communication.Translator;
import mdp.simulation.GUI;

public class Main {
    
    public static GUI _gui;
    
    public static void main(String[] args) throws IOException {
                
        // run simulation
        System.out.println("Initiating GUI...");
        _gui = new GUI();
        
        // connect & send string to RPi
        //_checkRPiConnection();

    }
    
    public static GUI getGUI() {
        return _gui;
    }
    
    private static void _checkRPiConnection() throws IOException {
        List<RobotAction> test = new ArrayList<>();
        test.add(RobotAction.RotateLeft);
        test.add(RobotAction.RotateRight);
        test.add(RobotAction.MoveForward);
        test.add(RobotAction.MoveBackward);
        Translator translator = new Translator();
        translator.sendToArduino(test);
        translator.listen(() -> {
            String inStr = translator.getInputBuffer();
        });
        
//        while (true) {
//            String received = translator.readAsString();
//            if (received.length() != 0) {
//                System.out.println(received);
//            }
//        }
    }
    
}
