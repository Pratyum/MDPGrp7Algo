package mdp;

import java.io.IOException;
import mdp.solver.Solver;
import java.util.List;
import java.util.ArrayList;
import mdp.communication.SocketCommunicator;
import mdp.communication.Translator;
import mdp.solver.shortestpath.AStarSolver;

public class Main {
    
    private static int[][] _map = 
    {
        { 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0 },
        { 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };
    
    public static void main(String[] args) throws IOException {
        // initialize map
        Map map = new Map();
         
        // initialize robot
        Vector2 robotPos = new Vector2(3, 2);
        Direction robotDir = Direction.Right;
        Robot robot = new Robot(robotPos, robotDir);
        
        // put some blockers into the map
        map.addObstacle(_genBlockers(_map));
        
        Solver solver = new AStarSolver();
        List<Vector2> solveResult = solver.solve(map, robot);
        map.highlight(solveResult);
        
        if (solveResult.size() != 0) {
            System.out.println("Solution found:");
            solveResult.forEach((point) -> {
                System.out.println(point.toString());
            });
        } else {
            System.out.println("No solution.");
        }
        System.out.println();
        System.out.println(map.toString(robot));
        
        
        List<RobotAction> test = new ArrayList<RobotAction>();
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
    
    private static List<Vector2> _genBlockers(int[][] obstacleMap) {
        List<Vector2> blockers = new ArrayList<>();
        for (int i = 0; i < obstacleMap.length; i++) {
            for (int j = 0; j < obstacleMap[0].length; j++) {
                if (obstacleMap[i][j] == 1) {
                    blockers.add(new Vector2(i, j));
                }
            }
        }
        return blockers;
    }
    
}