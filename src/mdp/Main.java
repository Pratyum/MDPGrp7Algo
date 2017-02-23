package mdp;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import mdp.communication.Translator;
import mdp.simulation.GUI;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class Main {
    
    private static int[][] _map = 
    {
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };
    
    public static void main(String[] args) throws IOException {
        // initialize map
        Map map = new Map();
        map.addObstacle(_genBlockers(_map));
         
        // initialize robot
        Vector2 robotPos = new Vector2(1, 1);
        Direction robotDir = Direction.East;
        Robot robot = new Robot(robotPos, robotDir);
        
        // solve map for shortest path
        AStarSolverResult solveResult = _runShortestPath(map, robot);
        LinkedList<RobotAction> shortestPathActions = RobotAction
                                    .fromPath(robot, solveResult.shortestPath);
        
        // run simulation
        System.out.println("Initiating GUI...");
        GUI gui = new GUI();
        gui.update(map, robot);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!shortestPathActions.isEmpty()) {
                    robot.execute(shortestPathActions.pop());
                    gui.update(map, robot);
                } else {
                    this.cancel();
                }
            }
        }, 200, 200);
        
        // connect & send string to RPi
//        checkRPiConnection();

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
    
    private static AStarSolverResult _runShortestPath(Map map, Robot robot) {
        AStarSolver solver = new AStarSolver();
        AStarSolverResult solveResult = solver.solve(map, robot);
        map.highlight(solveResult.openedPoints, WPSpecialState.IsOpenedPoint);
        map.highlight(solveResult.closedPoints, WPSpecialState.IsClosedPoint);
        map.highlight(solveResult.shortestPath, WPSpecialState.IsPathPoint);
        
        System.out.println("Points opened: " + solveResult.openedPoints.size());
        System.out.println("Points closed: " + solveResult.closedPoints.size());
        
        if (!solveResult.shortestPath.isEmpty()) {
            System.out.println("Solution found (" + 
                                (solveResult.shortestPath.size() + 1) + 
                                " steps):");
        } else {
            System.out.println("No solution.");
        }
//        solveResult.shortestPath.forEach((curPos) -> {
//            System.out.println(curPos.toString());
//        });
//        System.out.println();
//        System.out.println(map.toString(robot));

        return solveResult;
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
