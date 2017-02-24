package mdp.simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.common.Vector2;
import mdp.map.WPObstacleState;

public class GridContainer extends JPanel {
    
    private static final int _DIM_ROW = 15;
    private static final int _DIM_COL = 20;
    private static final int _SQUARE_SIZE = 30;
    private static final int _GAP = 1;
    
    private GridSquare[][] _grid;
    private MouseAdapter _gridMouseAdapter;

    public GridSquare[][] getGrid() {
        return _grid;
    }

    public GridContainer() {
        // config
        this.setLayout(new FlowLayout(FlowLayout.LEFT, _GAP, _GAP));
        this.setPreferredSize(new Dimension(
            _DIM_COL * _SQUARE_SIZE + (_DIM_COL + 1) * _GAP,
            _DIM_ROW * _SQUARE_SIZE + (_DIM_ROW + 1) * _GAP
        ));
        this.setBackground(ColorConfig.BG);
        
        // children
        this.fillGrid(new Map(), new Robot());
    }
    
    private List<Vector2> _gen3x3(Vector2 curPos) {
        List<Vector2> result = new ArrayList<>();
        result.add(curPos);
        result.add(curPos.fnAdd(new Vector2(0, 1)));
        result.add(curPos.fnAdd(new Vector2(0, -1)));
        result.add(curPos.fnAdd(new Vector2(1, 0)));
        result.add(curPos.fnAdd(new Vector2(-1, 0)));
        result.add(curPos.fnAdd(new Vector2(1, 1)));
        result.add(curPos.fnAdd(new Vector2(1, -1)));
        result.add(curPos.fnAdd(new Vector2(-1, 1)));
        result.add(curPos.fnAdd(new Vector2(-1, -1)));
        return result;
    }
    
    private int _getColorOrder(Color color) {
        if (color.equals(ColorConfig.ROBOT_HEAD)) {
            return 0;
        } else if (color.equals(ColorConfig.ROBOT_BODY)) {
            return -1;
        } else if (color.equals(ColorConfig.PATH) || 
                    color.equals(ColorConfig.OPENED) || 
                    color.equals(ColorConfig.CLOSED)) {
            return -2;
        } else {
            return -3;
        }
    }
    
    private Color _resolveColor(String curKey, Color targetColor, HashMap<String, Color> specialColors) {
        Color existingColor = specialColors.getOrDefault(curKey, ColorConfig.NORMAL);
        return _getColorOrder(existingColor) > _getColorOrder(targetColor) ? 
                existingColor : targetColor;
    }
    
    private boolean _colorOverideCheck(Vector2 curPos, HashMap<String, Color> specialColors) {
        if (specialColors.containsKey(curPos.toString())) {
            if (specialColors.get(curPos.toString()).equals(ColorConfig.PATH)) return false;
            for (Vector2 adjPos : _gen3x3(curPos)) {
                if (specialColors.containsKey(adjPos.toString())) {
                    if (specialColors.get(adjPos.toString()).equals(ColorConfig.ROBOT_BODY)) return false;
                    if (specialColors.get(adjPos.toString()).equals(ColorConfig.ROBOT_HEAD)) return false;
                }
            }
        }
        return true;
    }
    
    private HashMap<String, Color> _genSpecialColor(Map map, Robot robot) {
        HashMap<String, Color> result = new HashMap<>();
        map.toList().forEach((curPoint) -> {
            String curKey;
            // 1x1
            // actual obstacle
            curKey = curPoint.position().toString();
            if (curPoint.obstacleState().equals(WPObstacleState.IsActualObstacle)) {
                result.put(curKey, _resolveColor(curKey, ColorConfig.OBSTACLE, result));
            }
            
            // 3x3
            switch (curPoint.specialState()) {
                case IsStart:
                    for (Vector2 curPos : _gen3x3(curPoint.position())) {
                        curKey = curPos.toString();
                        result.put(curKey, _resolveColor(curKey, ColorConfig.START, result));
                    }
                    break;
                case IsGoal:
                    for (Vector2 curPos : _gen3x3(curPoint.position())) {
                        curKey = curPos.toString();
                        result.put(curKey, _resolveColor(curKey, ColorConfig.GOAL, result));
                    }
                    break;
                case IsPathPoint:
                    curKey = curPoint.position().toString();
                    result.put(curKey, _resolveColor(curKey, ColorConfig.PATH, result));
                    break;
                case IsClosedPoint:
                    curKey = curPoint.position().toString();
                    result.put(curKey, _resolveColor(curKey, ColorConfig.CLOSED, result));
                    break;
                case IsOpenedPoint:
                    curKey = curPoint.position().toString();
                    result.put(curKey, _resolveColor(curKey, ColorConfig.OPENED, result));
                    break;
            }
            // robot
            if (curPoint.position().equals(robot.position())) {
                for (Vector2 curPos : _gen3x3(curPoint.position())) {
                    Color robotSqColor = ColorConfig.ROBOT_BODY;
                    if (robot.position()
                            .fnAdd(robot.orientation().toVector2())
                            .equals(curPos)) {
                        robotSqColor = ColorConfig.ROBOT_HEAD;
                    }
                    curKey = curPos.toString();
                    result.put(curKey, _resolveColor(curKey, robotSqColor, result));
                }
            }
        });
        
        return result;
    }
    
    private void _applyMouseAdapter() {
        for (GridSquare[] row : _grid) {
            for (GridSquare square : row) {
                for (MouseListener mouseListener : square.getMouseListeners()) {
                    square.removeMouseListener(mouseListener);
                }
                square.addMouseListener(_gridMouseAdapter);
            }
        }
    }
    
    public void fillGrid(Map map, Robot robot) {
        boolean isFirstTime = _grid == null;
//        this.removeAll();
        if (isFirstTime) _grid = new GridSquare[_DIM_ROW][_DIM_COL];

        HashMap<String, Color> specialColors = _genSpecialColor(map, robot);

        for (int i = 0; i < _DIM_ROW; i++) {
            for (int j = 0; j < _DIM_COL; j++) {
                if (isFirstTime) _grid[i][j] = new GridSquare(new Vector2(i, j));

                Vector2 curLocation = new Vector2(i, j);
                Color targetColor = ColorConfig.NORMAL;
                if (specialColors.containsKey(curLocation.toString())) {
                    targetColor = specialColors.get(curLocation.toString());
                }
                _grid[i][j].setBackground(targetColor);                    
                
                if (isFirstTime) {
                    _grid[i][j].setPreferredSize(new Dimension(
                        _SQUARE_SIZE,
                        _SQUARE_SIZE
                    ));
                }
            }
        }
        if (isFirstTime) {
            for (GridSquare[] row : _grid) {
                for (GridSquare square : row) {
                    this.add(square);
                }
            }
        }
    }
    
    public void setGridAdapter(MouseAdapter adapter) {
        _gridMouseAdapter = adapter;
        _applyMouseAdapter();
    }
    
}
