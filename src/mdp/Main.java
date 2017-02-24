package mdp;

import mdp.robot.RobotAction;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import mdp.communication.Translator;
import mdp.simulation.GUI;

public class Main {
    
    public static void main(String[] args) throws IOException {
        
        // run simulation
        System.out.println("Initiating GUI...");
        GUI gui = new GUI();
        
        // connect & send string to RPi
//        _checkRPiConnection();

    }
    
    private static void _checkRPiConnection() throws IOException {
        List<RobotAction> test = new ArrayList<>();
        test.add(RobotAction.RotateLeft);
        test.add(RobotAction.RotateRight);
        test.add(RobotAction.MoveForward);
        test.add(RobotAction.MoveBackward);
        Translator translator = new Translator();
        translator.sendToArduino(test);
        
        while (true) {
            String received = translator.readFromArduino();
            if (received.length() != 0) {
                System.out.println(received);
            }
        }
    }
    
}
