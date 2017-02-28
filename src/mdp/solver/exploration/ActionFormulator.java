package mdp.solver.exploration;

import mdp.map.Waypoint;
import mdp.robot.Robot;
import mdp.robot.RobotAction;

import mdp.common.Direction;
import mdp.map.Map;

public class ActionFormulator {

    private static MapViewer mapViewer;

    private static Simulator simulator;

    public ActionFormulator(MapViewer mapV, Simulator s) {
        mapViewer = mapV;
        simulator = s;
    }

    //got to init map . can we inject map instead?
    public String actionToTake(Waypoint frontier, Robot robot) {

        String result = "";

        return result;

    }

    public void rightWallFollower(Robot robot) throws InterruptedException {

        view(robot); // for scanning purpose

        if (null != mapViewer.checkWalkable(robot, Direction.Right)) {
            switch (mapViewer.checkWalkable(robot, Direction.Right)) {
                case Yes:
                    robot.bufferAction(RobotAction.RotateRight);
                    robot.bufferAction(RobotAction.MoveForward);
                    break;
                case No:
                    turnLeftTillEmpty(robot); //now didnt turn left , so execute directly
                    break;
                case Unsure:
                    robot.bufferAction(RobotAction.RotateRight);
                    view(robot);
                    if (null == mapViewer.checkWalkable(robot, Direction.Up)) {
                        System.out.println("Error1");
                    } else {
                        switch (mapViewer.checkWalkable(robot, Direction.Up)) {
                            case Yes:
                                robot.bufferAction(RobotAction.MoveForward);
                                break;
                            case No:
                                robot.bufferAction(RobotAction.RotateLeft);
                                turnLeftTillEmpty(robot);
                                break;
                            default:
                                System.out.println("Error1");
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        // if all around empty, avoid turning in a loop, find the wall directly
        if (mapViewer.checkAllAroundEmpty(robot) == Know.Yes) {

            robot.bufferAction(RobotAction.RotateRight);
            robot.bufferAction(RobotAction.MoveForward);
        }

    }

    // look through map and update 
    public static Map view(Robot robot) throws InterruptedException {
        if (robot.checkIfHavingBufferActions()) {
            robot.executeBufferActions(ExplorationSolver.getExePeriod());
        }

        SensingData s;
        s = simulator.getSensingData(robot);
        Map subjective_map = mapViewer.updateMap(robot, s);
        System.out.println(mapViewer.exploredAreaToString());
        System.out.println(subjective_map.toString(robot));

        return subjective_map;
    }

    public static void turnLeftTillEmpty(Robot robot) throws InterruptedException {

        Know check = mapViewer.checkWalkable(robot, Direction.Up);

        if (check == Know.Unsure) {
            view(robot);
        }
        // make sure it is viewed before turn
        //update
        check = mapViewer.checkWalkable(robot, Direction.Up);

        if (check == Know.Yes) {
            robot.bufferAction(RobotAction.MoveForward);
            return;
        }

        if (check == Know.No) {
            robot.bufferAction(RobotAction.RotateLeft);
            turnLeftTillEmpty(robot);

        }

    }
}
