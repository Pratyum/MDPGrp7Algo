package mdp.solver.exploration;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import mdp.common.Direction;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.common.Vector2;

public class ExplorationSolver {

    private static Map objective_map; // generated from map solver

    private static Simulator simulator;

    private static MapViewer mapViewer = new MapViewer();
    private static ActionFormulator actionFormulator;
    private static GoalFormulator goalFormulator = new GoalFormulator(mapViewer);

    private static int _exePeriod;

    public static void main(Map map, int exePeriod) throws InterruptedException {
        _exePeriod = exePeriod;
        objective_map = map;
        simulator = new Simulator(objective_map);

        Vector2 robotPos = new Vector2(1, 1);
        Direction robotDir = Direction.Down;
        Robot robot = new Robot(robotPos, robotDir);
        actionFormulator = new ActionFormulator(mapViewer, simulator);
        SensingData s;
        int x;

        // put some blockers into the map
        System.out.println(objective_map.toString(robot));

        //data = getDataFromRPI();
        while (!goalFormulator.checkIfReachFinalGoal(robot.position())) {
            actionFormulator.rightWallFollower(robot);
        }
        while (!goalFormulator.checkIfReachStartZone(robot.position())) {

            actionFormulator.rightWallFollower(robot);

        }
        ArrayList<Pair<Integer,Integer>> unexplored = getUnExplored();
        System.out.println("i:" + robot.position().i());
        System.out.println("j:" + robot.position().j());

    }

    public static int getExePeriod() {
        return _exePeriod;
    }

    public static MapViewer getMapViewer() {
        return mapViewer;
    }
    public static ArrayList<Pair<Integer,Integer>> getUnExplored(){
        int [][] explored = mapViewer.getExplored();
        ArrayList<Pair<Integer,Integer>> unexplored = new ArrayList<Pair<Integer, Integer>>() ;
        for(int row=0;row<10;++row){
            for(int col=0;col<20;++col){
                if(explored[row][col]==0){
                    System.out.println(String.valueOf(row) + "," + String.valueOf(col));
                    Pair<Integer,Integer> pair = new Pair<Integer,Integer>(row, col);
                    unexplored.add(pair);
                }
            }
        }
        return unexplored;
    }
    
}
