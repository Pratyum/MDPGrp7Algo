package mdp.map;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import mdp.common.Vector2;

public class Descriptor {
    
    private static String _getFilePath(String fileName) throws IOException {
        String rootPath = new File(".").getCanonicalPath() + "//src//";
        String packagePath = Descriptor.class.getPackage().getName().replace(".", "//") + "//";
        String mapDirPath = "test//";
        String fileExtension = ".txt";
        return rootPath + packagePath + mapDirPath + fileName + fileExtension;
    }
    
    private static String _binToHex(String bin) {
        String result = "";
        
        // ensure bin length is multiples of 4
        String fixedBin = bin;
        System.out.println("before: " + fixedBin.length());
        while (fixedBin.length() % 4 != 0) {
            fixedBin += "0";
        }
        System.out.println("after: " + fixedBin.length());
        
        // translate each blocks of 4 bits
        for (int i = 0; i < fixedBin.length(); i += 4) {
            String curSection = fixedBin.substring(i, i + 4);
            int sectionDec = 0;
            for (int j = 0; j < curSection.length(); j++) {
                if (curSection.charAt(curSection.length() - 1 - j) == '1') {
                    sectionDec += Math.pow(2, j);
                }
            }
            if (sectionDec > 9) {
                // A -> F
                result += (char) (sectionDec % 9 + 64);
            } else {
                // 0 -> 9
                result += sectionDec;
            }
        }
        
        return result;
    }
    
    public static String[] toHex(String desc) {
        String[] result = new String[2];
        int resultIndex = 0;
        for (String curLine : desc.split("\n")) {
            if (curLine.equals("11")) {
                if (result[0] != null && !result[0].isEmpty()) {
                    resultIndex++;
                }
            } else {
                if (result[resultIndex] == null) result[resultIndex] = "";
                result[resultIndex] += curLine;
            }            
        }
        result[0] = _binToHex("11" + result[0] + "11");
        result[1] = _binToHex(result[1]);
        return result;
    }
    
    public static String stringify(Map map, int[][] explored) {
        String result = "";
        
        result += "11\n";
        
        for (int descI = 0; descI < Map.DIM_J; descI++) {
            for (int descJ = 0; descJ < Map.DIM_I; descJ++) {
                result += explored[descJ][descI] >= 1 ? "1" : "0";
            }
            result += "\n";
        }
        
        result += "11\n";
        
        for (int descI = 0; descI < Map.DIM_J; descI++) {
            for (int descJ = 0; descJ < Map.DIM_I; descJ++) {
                int curPoint = explored[descJ][descI];
                if (curPoint >= 1) {
                    result += curPoint == 2 ? "1" : "0";
                }
            }
            result += "\n";
        }
        System.out.println(result);
//        System.out.println("EXPLORED");
//        for (int i = 0; i < explored.length; i++) {
//            int[] row = explored[i];
//            for (int j = 0; j < row.length; j++) {
//                int p = row[j];
//                System.out.print(p + " ");
//            }
//            System.out.print("\n");
//        }
//        System.out.println();
        return result;
    }
    
    public static String stringify(Map map, boolean[][] explored) {
        String result = "";
        
        result += "11\n";
        
        int[] descRowLength = new int[Map.DIM_J];
        for (int descI = 0; descI < Map.DIM_J; descI++) {
            descRowLength[descI] = 0;
        }
        
        for (int descI = 0; descI < Map.DIM_J; descI++) {
            for (int descJ = 0; descJ < Map.DIM_I; descJ++) {
                result += explored[descJ][descI] ? "1" : "0";
                descRowLength[descI] += explored[descJ][descI] ? 1 : 0;
            }
            result += "\n";
        }
        
        result += "11\n";
        
        for (int descI = 0; descI < Map.DIM_J; descI++) {
            for (int descJ = 0; descJ < descRowLength[descI]; descJ++) {
//                System.out.println(new Vector2(Map.DIM_J - 1 - descJ, Map.DIM_I - 1 - descI));
                boolean isObstacle = map
                    .getPoint(new Vector2(Map.DIM_I - 1 - descJ, Map.DIM_J - 1 - descI))
                        .obstacleState().equals(WPObstacleState.IsActualObstacle);
                result += isObstacle ? "1" : "0";
            }
            result += "\n";
        }
        
        return result;
    }
    
    public static void saveToFile(String filePath, Map map, boolean[][] explored) throws IOException {
        PrintWriter writer;
        
        if (filePath.contains("\\")) {
            writer = new PrintWriter(filePath);
        } else {
            writer = new PrintWriter(_getFilePath(filePath));
        }
        
        writer.write(stringify(map, explored));
        writer.close();
    }
    
    public static String readFile(String filePath) throws IOException {
        String result = "";
        
        File file;
        if (filePath.contains("//")) {
            file = new File(filePath);
        } else {
            file = new File(_getFilePath(filePath));
        }
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                result += scanner.nextLine() + "\n";
            }
        }
        
        return result;
    }
    
    public static Map parseFromFile(String filePath) throws IOException {
        Map result = new Map();
        
        File file;
        if (filePath.contains("//")) {
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
                        result.addObstacle(new Vector2(
                            Map.DIM_I - 1 - descPos.j(),
                            Map.DIM_J - 1 - descPos.i()
                        ));
                    }
                }
            });
        }
        
        return result;
    }
    
}
