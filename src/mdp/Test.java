package mdp;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import mdp.common.Vector2;
import mdp.communication.ITranslatable;
import mdp.communication.Translator;
import mdp.map.Map;
import mdp.map.WPSpecialState;
import mdp.robot.Robot;
import mdp.solver.shortestpath.AStarSolver;
import mdp.solver.shortestpath.AStarUtil;

public class Test {
    
    public static void run() throws IOException {
        
        AStarSolver solver = new AStarSolver();
        Robot robot = new Robot();
        Map map = new Map();
        
        
        List<Vector2> obs = new LinkedList<>();
        obs.add(new Vector2(2, 13));
        obs.add(new Vector2(4, 13));
//        obs.add(new Vector2(9, 15));
        obs.add(new Vector2(8, 12));
        obs.add(new Vector2(11, 9));
        obs.add(new Vector2(14, 6));
        obs.add(new Vector2(7, 3));
        obs.add(new Vector2(0, 7));
        obs.add(new Vector2(3, 7));
        obs.add(new Vector2(6, 7));
//        obs.add(new Vector2(5, 9));
//        obs.add(new Vector2(6, 10));
        map.addObstacle(obs);
        System.out.println(map.toString(robot));
        
        
        List<Vector2> path = solver.solve(map, robot, true).shortestPath;
        System.out.println("Path");
        path.forEach((pos) -> {
            System.out.println(pos);
        });
        map.highlight(path, WPSpecialState.IsPathPoint);
        System.out.println(map.toString(robot));
        
        
        List<Vector2> smoothPath = AStarUtil.smoothenPath(map, path, false);
        System.out.println("Smooth Path");
        smoothPath.forEach((pos) -> {
            System.out.println(pos);
        });
        map.highlight(smoothPath, WPSpecialState.IsOpenedPoint);
        System.out.println(map.toString(robot));
        
        
        ITranslatable rpi = new Translator();
        rpi.sendSmoothMoveCommand(smoothPath);
    }
    
}
