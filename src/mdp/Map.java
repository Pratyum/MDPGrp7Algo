package mdp;

import java.util.List;

public class Map {
    // constants
    public final int DIM_I = 15;
    public final int DIM_J = 20;
    public final Vector2 START_POS = new Vector2(1, 1);
    public final Vector2 GOAL_POS = new Vector2(DIM_I - 2, DIM_J - 2);
    
    private final Waypoint[][] _wpMap;
    
    public Map() {
        _wpMap = new Waypoint[DIM_I][DIM_J];
        for (int i = 0; i < DIM_I; i++) {
            for (int j = 0; j < DIM_J; j++) {
                // init default values
                Vector2 curPos = new Vector2(i, j);
                WPSpecialState curSpecState = WPSpecialState.NA;
                WPObstacleState curObsState = WPObstacleState.IsWalkable;
                
                // change special state if appicable
                if (START_POS.equals(curPos)) {
                    curSpecState = WPSpecialState.IsStart;
                } else if (GOAL_POS.equals(curPos)) {
                    curSpecState = WPSpecialState.IsGoal;
                } else if (i == 0 || j == 0 || i == DIM_I - 1 || j == DIM_J - 1) {
                    curObsState = WPObstacleState.IsVirtualObstacle;
                }
                
                // create point
                _wpMap[i][j] = new Waypoint(curPos, curSpecState, curObsState);
            }
        }
    }
    
    private void _setObs(Vector2 pos, WPObstacleState obsState) {
        _wpMap[pos.i()][pos.j()].obstacleState(obsState);
    }
    
    public void addObstacle(List<Vector2> obstaclePositions) {
        obstaclePositions.forEach((curObsPos) -> {
            // add blocking tag for the obstacle
            _setObs(curObsPos, WPObstacleState.IsActualObstacle);
            
            // add blocking tag for points adjacent to the obstacle
            for (int deltaI = -1; deltaI <= 1; deltaI++) {
                for (int deltaJ = -1; deltaJ <= 1; deltaJ++) {
                    int adjI = curObsPos.i() + deltaI;
                    int adjJ = curObsPos.j() + deltaJ;
                    if (adjI > -1 && adjI < DIM_I && adjJ > -1 && adjJ < DIM_J &&
                        _wpMap[adjI][adjJ].obstacleState() == WPObstacleState.IsWalkable) {
                        Vector2 adjacentPos = new Vector2(adjI, adjJ);
                        _setObs(adjacentPos, WPObstacleState.IsVirtualObstacle);
                    }
                }
            }
        });
    }
    public void highlight(List<Vector2> hightlightPositions) {
        hightlightPositions.forEach((pos) -> {
            _wpMap[pos.i()][pos.j()].specialState(WPSpecialState.IsHighlighted);
        });
    }
    public String toString(Robot robot) {
        String result = "";
        for (int i = -1; i <= DIM_I; i++) {
            for (int j = -1; j <= DIM_J; j++) {
                if (i == -1 || j == -1 || i == DIM_I || j == DIM_J) {
                    result += "# ";
                } else {
                    Waypoint curPoint = _wpMap[i][j];
                    if (curPoint.position().equals(robot.position())) {
                        String symbol = "  ";
                        switch (robot.direction()) {
                            case Up:
                                symbol = "^ ";
                                break;
                            case Down:
                                symbol = "_ ";
                                break;
                            case Left:
                                symbol = "< ";
                                break;
                            case Right:
                                symbol = "> ";
                                break;
                        }
                        result += Console.ANSI_RED + symbol + Console.ANSI_RESET;
                        continue;
                    }
                    switch (curPoint.specialState()) {
                        case IsStart:
                            result += Console.ANSI_CYAN + "S " + Console.ANSI_RESET;
                            continue;
                        case IsGoal:
                            result += Console.ANSI_PURPLE + "G " + Console.ANSI_RESET;
                            continue;
                        case IsHighlighted:
                            result += Console.ANSI_GREEN + "x " + Console.ANSI_RESET;
                            continue;
                        case NA:
                            break;
                    }
                    switch (curPoint.obstacleState()) {
                        case IsActualObstacle:
                            result += Console.ANSI_WHITE_BACKGROUND + " " + Console.ANSI_RESET + " ";
                            break;
                        case IsVirtualObstacle:
                            result += "  ";
                            break;
                        case IsWalkable:
                            result += Console.ANSI_GRAY + "+ " + Console.ANSI_RESET;
                            break;
                    }
                }
            }
            result += "\n";
        }
        return result;
    }
    
    public Waypoint getPoint(Vector2 position) {
        return _wpMap[position.i()][position.j()];
    }
    
    public boolean checkValidPosition(Vector2 pos) {
        return pos.i() > 0 && pos.i() < DIM_I &&
               pos.j() > 0 && pos.j() < DIM_J;
    }
    
}