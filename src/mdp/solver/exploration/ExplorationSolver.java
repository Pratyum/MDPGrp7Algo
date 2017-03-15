package mdp.solver.exploration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import mdp.Main;
import mdp.common.Direction;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.robot.RobotAction;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarSolverResult;

public class ExplorationSolver {

//    public enum EndType { Normal, Interrupt }
    private static Map objective_map; // generated from map solver

    private static Simulator simulator;

    private static MapViewer mapViewer;
    private static ActionFormulator actionFormulator;
    private static GoalFormulator goalFormulator;

    private static int _exePeriod;
    private static Robot _robot;

    public static void solve(Map map, int exePeriod) throws InterruptedException, IOException {
        mapViewer = new MapViewer();
        goalFormulator = new GoalFormulator(mapViewer);
        _exePeriod = exePeriod;
        objective_map = map;
        simulator = new Simulator(objective_map);

        Vector2 robotPos = new Vector2(1, 1);
        Direction robotDir = Direction.Right;
        actionFormulator = new ActionFormulator(mapViewer, simulator);
        _robot = new Robot(robotPos, robotDir, mapViewer, actionFormulator);

        Direction last_orientation;
        LinkedList<Direction> twoDirectionAway = new LinkedList<Direction>();

        // put some blockers into the map
        System.out.println("For Simulation Purpose");
        System.out.println(objective_map.toString(_robot));

        //data = getDataFromRPI();
        System.out.println(_robot.position());

        //default to rotate in order to get initial sensing data
        _robot.bufferAction(RobotAction.RotateRight);
        _robot.executeBufferActions(ExplorationSolver.getExePeriod());;
        ////////////
        //start exploration

        twoDirectionAway.add(Direction.Down);
        twoDirectionAway.add(Direction.Down);

        int counter = 0;
        Vector2 last_position = new Vector2(-1, -1);
        while (!goalFormulator.checkIfReachFinalGoal(_robot.position())) {
            //System.out.println("following right wall");

            //System.out.println(mapViewer.getSubjectiveMap().toString(_robot));
            last_position = new Vector2(_robot.position().i(), _robot.position().j());
            last_orientation = _robot.orientation();
            actionFormulator.rightWallFollower(_robot);

            System.out.println("Last position: " + last_position.toString());
            System.out.println("Current position: " + _robot.position().toString());
            System.out.println("last orientation: " + last_orientation.toString());
            //System.out.println(mapViewer.exploredAreaToString());
            //System.out.println(mapViewer.robotVisitedPlaceToString());
            //System.out.println(mapViewer.getSubjectiveMap().toString(_robot));

            if (_robot.position().equals(last_position.fnAdd(last_orientation.getRight().toVector2()))) {

                counter++;
            } else {
                counter = 0;
            }

            if (counter == 7) {
                _robot.bufferAction(RobotAction.RotateRight);
                _robot.bufferAction(RobotAction.RotateRight);
                _robot.bufferAction(RobotAction.MoveForward);
                _robot.bufferAction(RobotAction.RotateRight);
                actionFormulator.view(_robot);
                counter = 0;
            }
            System.out.println("Counter is " + counter);

        }
        while (!goalFormulator.checkIfReachStartZone(_robot.position())) {
            last_position = new Vector2(_robot.position().i(), _robot.position().j());
            last_orientation = _robot.orientation();
            actionFormulator.rightWallFollower(_robot);

            System.out.println("Last position: " + last_position.toString());
            System.out.println("Current position: " + _robot.position().toString());
            System.out.println("last orientation: " + last_orientation.toString());
            //System.out.println(mapViewer.exploredAreaToString());
            //System.out.println(mapViewer.robotVisitedPlaceToString());
            //System.out.println(mapViewer.getSubjectiveMap().toString(_robot));

            if (_robot.position().equals(last_position.fnAdd(last_orientation.getRight().toVector2()))) {

                counter++;
            } else {
                counter = 0;
            }

            if (counter == 7) {
                _robot.bufferAction(RobotAction.RotateRight);
                _robot.bufferAction(RobotAction.RotateRight);
                _robot.bufferAction(RobotAction.MoveForward);
                _robot.bufferAction(RobotAction.RotateRight);
                actionFormulator.view(_robot);
                counter = 0;
            }
            System.out.println("Counter is " + counter);

        }

        actionFormulator.exploreRemainingArea(_robot);

    }

    public static int getExePeriod() {
        return _exePeriod;
    }

    public static MapViewer getMapViewer() {
        return mapViewer;
    }

    public static Robot getRobot() {
        return _robot;
    }

    // look through map and update 
    public static void goBackToStart(Map map, Robot robot, Runnable callback) {
        System.out.println("Going back to start with the following map");
        System.out.println(map.toString(robot));
        AStarSolverResult result = new AStarSolver().solve(map, robot, Map.START_POS);
        LinkedList<RobotAction> actions = RobotAction.fromPath(robot, result.shortestPath);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!actions.isEmpty()) {
                    RobotAction action = actions.pop();
                    robot.execute(action);
                    Main.getGUI().update(robot);
                    if (!Main.isSimulating()) {
                        LinkedList<RobotAction> curAct = new LinkedList<>();
                        curAct.add(action);
                        Main.getRpi().sendMoveCommand(curAct);
                    }
                } else {
                    Robot defaultRobot = new Robot();
                    if (defaultRobot.orientation().equals(robot.orientation().getLeft())) {
                        robot.execute(RobotAction.RotateLeft);
                        if (!Main.isSimulating()) {
                            LinkedList<RobotAction> curAct = new LinkedList<>();
                            curAct.add(RobotAction.RotateLeft);
                            Main.getRpi().sendMoveCommand(curAct);
                        }
                    } else if (defaultRobot.orientation().equals(robot.orientation().getRight())) {
                        robot.execute(RobotAction.RotateRight);
                        if (!Main.isSimulating()) {
                            LinkedList<RobotAction> curAct = new LinkedList<>();
                            curAct.add(RobotAction.RotateRight);
                            Main.getRpi().sendMoveCommand(curAct);
                        }
                    } else if (defaultRobot.orientation().equals(robot.orientation().getBehind())) {
                        robot.execute(RobotAction.RotateLeft);
                        robot.execute(RobotAction.RotateLeft);
                        if (!Main.isSimulating()) {
                            LinkedList<RobotAction> curAct = new LinkedList<>();
                            curAct.add(RobotAction.RotateLeft);
                            curAct.add(RobotAction.RotateLeft);
                            Main.getRpi().sendMoveCommand(curAct);
                        }
                    }
                    timer.cancel();
                    System.out.println("Starting callback");
                    callback.run();
                }
            }
        }, _exePeriod, _exePeriod);
    }

    public static void restart() {
    }
}
