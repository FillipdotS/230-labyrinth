package source.labyrinth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LevelReader {

    public static Board readFile(String filename) {
        Scanner in = null;
        try {
            in = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println("Can't find " + filename);
            System.exit(0);
        }
    return LevelReader.readLevel(in);
    }

    public static Board readLevel(Scanner in) {
        while(in.hasNext()) {
            int width = in.nextInt();
            int height = in.nextInt();
            int numOfFixedTiles = in.nextInt();
            int fixedTileStartPosX = in.nextInt();
            int startTilePosY = in.nextInt();
            String tileType = in.next();
            int orientation = in.nextInt();
            int playerStartPosX = in.nextInt();
            int playerStartPosY = in.nextInt();
            String typeOfTileInSilkbag = in.next();
            int totalNumOfSilkbagTiles = in.nextInt();

        }
        return readLevel(in);
    }
}
