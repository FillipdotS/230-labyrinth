package source.labyrinth;
import sun.util.logging.PlatformLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The LevelReader is going to read the level chosen, and pass that info so the Board can be constructed.
 * @author Ian Lavin Rady
 */

public class LevelReader {

	private static final int TOTAL_NUM_OF_PLAYERS = 4;

	/**
	 *
	 * @param filename the name of the file.
	 * @return the selected level once read.
	 */
	public static LevelData readDataFile(String filename) {
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
	public static LevelData level(Scanner in) {
		LevelData levelData = new LevelData();

		in.useDelimiter("(\\p{javaWhitespace}|,)+");
		while(in.hasNextInt()) {
			int profileID = in.nextInt();
		}

		in.nextLine();
		int width = in.nextInt();
		int height = in.nextInt();
		in.nextLine();

		Board levelBoard = new Board(width, height);
		levelData.setBoard(levelBoard);

		while(in.hasNext()) {
			int numOfFixedTiles = in.nextInt();
			in.nextLine();

			for(int i = 0; i < numOfFixedTiles; i++) {
				int xPos = in.nextInt();
				int yPos = in.nextInt();
				String type = in.next();
				int orientation = in.nextInt();

				FloorTile fixedTile = new FloorTile(orientation, FloorTile.TileType.valueOf(type));
				fixedTile.setFixed(true);
				levelBoard.setTileAt(fixedTile, xPos, yPos);

				in.nextLine();
			}

			int[][] playerStartingPositions = new int[4][2];
			for(int i = 0; i < TOTAL_NUM_OF_PLAYERS; i++) {
				playerStartingPositions[i][0] = in.nextInt();
				playerStartingPositions[i][1] = in.nextInt();
				in.nextLine();
			}
			levelData.setPlayerStartingPositions(playerStartingPositions);

			// Straight FloorTile
			levelData.setStraightAmount(in.nextInt());
			in.nextLine();

			// TShape FloorTile
			levelData.setTshapeAmount(in.nextInt());
			in.nextLine();

			// Corner FloorTile
			levelData.setCornerAmount(in.nextInt());
			in.nextLine();

			// Ice Action
			levelData.setIceAmount(in.nextInt());
			in.nextLine();

			// Fire Action
			levelData.setFireAmount(in.nextInt());
			in.nextLine();

			// DoubleMove Action
			levelData.setDoubleAmount(in.nextInt());
			in.nextLine();

			// Backtrack Action
			levelData.setBacktrackAmount(in.nextInt());
			in.nextLine();
		}

		return levelData;
	}

	private static void updateProfileID(String filename, int[] profileID ) {
		Scanner in = null;
		try {
			in = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Can't find " + filename);
			System.exit(0);
		}

		String[] pid = in.nextLine().split(",");
		for(int i = 0; i < profileID.length; i++) {
			if(checksIfExists(pid,profileID,i) == false);
			// I HAVE TO CALL FILEWRITER TO ADD PROFILEID[I] TO THIS LINE
			// System.out.println(profileID[i]);

		}
	}



	private static boolean checksIfExists(String[] pid,int[] profileID,int i) {
		for (int j = 0; j < pid.length; j++) {
			if (profileID[i] == Integer.parseInt(pid[j]))
				return true;
		}
		return false;
	}
}


