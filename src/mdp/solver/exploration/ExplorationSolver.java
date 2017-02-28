package mdp.solver.exploration;

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

        // put some blockers into the map
        System.out.println(objective_map.toString(robot));

        //data = getDataFromRPI();
        while (!goalFormulator.checkIfReachFinalGoal(robot.position())) {
            actionFormulator.rightWallFollower(robot);
        }
        while (!goalFormulator.checkIfReachStartZone(robot.position())) {

            actionFormulator.rightWallFollower(robot);

        }

        System.out.println("i:" + robot.position().i());
        System.out.println("j:" + robot.position().j());

    }

    public static int getExePeriod() {
        return _exePeriod;
    }

    public static MapViewer getMapViewer() {
        return mapViewer;
    }

}
