package mdp.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import mdp.common.Vector2;

public class Descriptor {
    
    private static String _getFilePath(String fileName) throws IOException {
        String rootPath = new File(".").getCanonicalPath() + "\\src\\";
        String packagePath = Descriptor.class.getPackage().getName().replace(".", "\\") + "\\";
        return rootPath + packagePath + fileName;
    }
    
    public static Map parseFromFile(String filePath) throws IOException {
        Map result = new Map();
        
        File file;
        if (filePath.contains("\\")) {
            file = new File(filePath);
        } else {
            file = new File(_getFilePath(filePath));
        }
        
        try (Scanner scanner = new Scanner(file)) {
            ArrayList<ArrayList<Vector2>> descExplored = new ArrayList<>();
            
            scanner.nextLine();
            
            // NOTE: descriptor format switch i & j coordinates
            // part 1
            for (int descI = 0; descI < Map.DIM_J; descI++) {
                String curLine = scanner.nextLine();
                ArrayList<Vector2> descRow = new ArrayList<>();
                for (int descJ = 0; descJ < curLine.length(); descJ++) {
                    if (curLine.charAt(descJ) == '1') {
                        descRow.add(new Vector2(descI, descJ));
                    }
                }
                descExplored.add(descRow);
            }
            
            scanner.nextLine();
            
            // part2
            descExplored.forEach((descRow) -> {
                String curLine = scanner.nextLine();
                for (int descJ = 0; descJ < curLine.length(); descJ++) {
                    if (curLine.charAt(descJ) == '1') {
                        Vector2 descPos = descRow.get(descJ);
                        result.addObstacle(new Vector2(descPos.j(), descPos.i()));
                    }
                }
            });
        }
        
        return result;
    }
    
}
