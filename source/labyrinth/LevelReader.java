package source.labyrinth;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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

	/**
	 * Update a level-specific leaderboard with new profiles
	 * @param levelName Level whose leaderboard will be changed
	 * @param profilesThatPlayed Integer ArrayList of profile ids that played on that level
	 * @param winningProfile Profile id of player that won (which should increase their wins by 1). Can be null
	 */
	public static void updateLeaderboard(String levelName, ArrayList<Integer> profilesThatPlayed, Integer winningProfile) {
		File leaderboardFile = new File("source/resources/leaderboards/" + levelName + "_leaderboard.ser");

		// If leaderboard file doesnt exist make one
		if (!leaderboardFile.exists()) {
			System.out.println("Leaderboard for " + levelName + " didn't exist, making one now...");
			try {
				leaderboardFile.createNewFile();
				// And fill it with an empty HashMap
				HashMap<Integer, Integer> empty = new HashMap<>();

				ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(leaderboardFile));
				objectOutputStream.writeObject(empty);
				objectOutputStream.flush();
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Could not create leaderboard file.");
			}
		}

		try {
			System.out.println("Attempting to read leaderboard for " + levelName);
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(leaderboardFile));

			HashMap<Integer, Integer> leaderboardInfo = (HashMap<Integer, Integer>) objectInputStream.readObject();
			objectInputStream.close();

			// Check that every profile that played exists in the leaderboard already.
			// If they don't, put them in with wins set to 0
			for (int i = 0; i < profilesThatPlayed.size(); i++) {
				int playingProfile = profilesThatPlayed.get(i);
				if (!leaderboardInfo.containsKey(playingProfile)) {
					leaderboardInfo.put(playingProfile, 0);
				}
			}

			// Update the winner with +1 wins
			// No need to check if the winner exists since the previous for-loop takes care of that
			if (winningProfile != null) {
				leaderboardInfo.put(winningProfile, leaderboardInfo.get(winningProfile) + 1);
			}

			// Now save the leaderboard
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(leaderboardFile));
			objectOutputStream.writeObject(leaderboardInfo);
			objectOutputStream.flush();
			objectOutputStream.close();
			System.out.println("Updated the leaderboard for " + levelName);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
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


