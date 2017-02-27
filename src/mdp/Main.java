package mdp;

import java.io.IOException;

import mdp.communication.Translator;
import mdp.communication.ITranslatable;
import mdp.simulation.GUI;
import mdp.simulation.IGUIUpdatable;

public class Main {
    
    public static IGUIUpdatable _gui;
    public static ITranslatable _rpi;
    
    public static void main(String[] args) throws IOException {
                
        // run simulation
        System.out.println("Initiating GUI...");
        _gui = new GUI();
        _rpi = new Translator();
        
        // connect & send string to RPi
//        _listenToRPi();

    }
    
    public static IGUIUpdatable getGUI() {
        return _gui;
    }
    
    private static void _listenToRPi() throws IOException {
        _rpi.listen(() -> {
            String inStr = _rpi.getInputBuffer();
            switch (inStr) {
                case "e":
                    _gui.trigger(IGUIUpdatable.ManualTrigger.Exploration);
                    break;
                case "s":
                    _gui.trigger(IGUIUpdatable.ManualTrigger.ShortestPath);
                    break;
                case "c":
                    _gui.trigger(IGUIUpdatable.ManualTrigger.Combined);
                    break;
                default:
                    break;
            }
        });
    }
    
}
