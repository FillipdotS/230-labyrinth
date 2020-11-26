package source.labyrinth;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * File is only missing constructors that need to be added to the classes so that I can call them here.
 * The LevelReader is going to read the level chosen, and pass that info so the Board can be constructed.
 * @author Ian Lavin Rady
 */

public class LevelReader {

    private static final int TOTAL_NUM_OF_PLAYERS = 4;
    private static final int TOTAL_NUM_OF_SILK_BAG_TILES = 7;

    /**
     *
     * @param filename the name of the file.
     * @return the selected level once read.
     */
    public static Board readDataFile(String filename) {
        Scanner in = null;
        try {
            in = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println("Can't find " + filename);
            System.exit(0);
        }
    return level(in);
    }

    /**
     *
     * @param in reads the input of the file.
     * @return the board width & height, details about every single fixed tile(type, orientation, position),
     *         the set of floor tiles that go into the silk bag, the set of action tiles that go into the silk bag &
     *         the four player starting positions.
     *
     */
    public static Board level(Scanner in) {
        int width = 0;
        int height =0;

        while(in.hasNext()) {
            in.useDelimiter("(\\p{javaWhitespace}|,)+");
            width = in.nextInt();
            height = in.nextInt();
            in.nextLine();
            int numOfFixedTiles = in.nextInt();
            in.nextLine();
            int[] fixedTileStartPosX = new int[numOfFixedTiles];
            int[] fixedStartTilePosY = new int[numOfFixedTiles];
            String[] tileType = new String[numOfFixedTiles];
            int[] orientation = new int[numOfFixedTiles];

            for(int i = 0; i < numOfFixedTiles; i++ ) {
                fixedTileStartPosX[i] = in.nextInt();
                fixedStartTilePosY[i] = in.nextInt();
                tileType[i] = in.next();
                orientation[i] = in.nextInt();
                in.nextLine();
            }
            int[] playerStartPosX = new int[TOTAL_NUM_OF_PLAYERS];
            int[] playerStartPosY = new int[TOTAL_NUM_OF_PLAYERS];
            for(int i = 0; i < TOTAL_NUM_OF_PLAYERS; i++) {
                playerStartPosX[i] = in.nextInt();
                playerStartPosY[i] = in.nextInt();
                in.nextLine();
            }
            int[] totalTileType= new int[TOTAL_NUM_OF_SILK_BAG_TILES];
            String[] tileTypeInSilkBag = new String[TOTAL_NUM_OF_SILK_BAG_TILES];
            for(int i = 0; i < TOTAL_NUM_OF_SILK_BAG_TILES; i++) {
                    tileTypeInSilkBag[i] = in.next();
                    System.out.println(tileTypeInSilkBag[i]);
                    totalTileType[i] = in.nextInt();
                    System.out.println(totalTileType[i]);
            }

        } Board levelBoard = new Board(width,height);
            return levelBoard;
    }
}
/*

                        int straight = in.nextInt();
                        in.nextLine();
                        int tShape = in.nextInt();
                        in.nextLine();
                        int corner = in.nextInt();
                        in.nextLine();
                        int ice = in.nextInt();
                        in.nextLine();
                        int fire = in.nextInt();
                        in.nextLine();
                        int doublemove = in.nextInt();
                        in.nextLine();
                        int backtrack = in.nextInt();*/
