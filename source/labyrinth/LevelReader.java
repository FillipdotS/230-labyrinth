package source.labyrinth;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The LevelReader is going to read the level chosen, and pass that info so the Board can be constructed.
 * @author Ian Lavin Rady
 * @author Fillip Serov
 */

public class LevelReader {

	private static final int TOTAL_NUM_OF_PLAYERS = 4;

	/**
	 *Reads file and handles exceptions in case file is not found
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

		while (in.hasNext()) {
			int numOfFixedTiles = in.nextInt();
			in.nextLine();

			for (int i = 0; i < numOfFixedTiles; i++) {
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
			for (int i = 0; i < TOTAL_NUM_OF_PLAYERS; i++) {
				playerStartingPositions[i][0] = in.nextInt();
				playerStartingPositions[i][1] = in.nextInt();
				in.nextLine();
			}
			levelData.setPlayerStartingPositions(playerStartingPositions);

			levelData.setFloorTileAmount(FloorTile.TileType.STRAIGHT, in.nextInt());
			in.nextLine();

			levelData.setFloorTileAmount(FloorTile.TileType.TSHAPE, in.nextInt());
			in.nextLine();

			levelData.setFloorTileAmount(FloorTile.TileType.CORNER, in.nextInt());
			in.nextLine();

			levelData.setFloorTileAmount(FloorTile.TileType.GOAL, in.nextInt());
			in.nextLine();

			levelData.setActionTileAmount(ActionTile.ActionType.ICE, in.nextInt());
			in.nextLine();

			levelData.setActionTileAmount(ActionTile.ActionType.FIRE, in.nextInt());
			in.nextLine();

			levelData.setActionTileAmount(ActionTile.ActionType.DOUBLEMOVE, in.nextInt());
			in.nextLine();

			levelData.setActionTileAmount(ActionTile.ActionType.BACKTRACK, in.nextInt());
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

	/**
	 * It reads the filename and adds new profileIDs if they don't already
	 * exist.
 	 * @param filename specified filename.
	 * @param profileID the Profile IDs to be updated.
	 */
	private static void updateProfileID(String filename, int[] profileID ) {
		Scanner in = null;
		try {
			in = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Can't find " + filename);
			System.exit(0);
		}

		String[] originalPid = in.nextLine().split(",");
		for (int i = 0; i < profileID.length; i++) {
			if (checkIfExists(originalPid,profileID,i) == false) {
				// I HAVE TO CALL FILEWRITER TO ADD PROFILEID[I] TO THIS LINE
				// System.out.println(profileID[i]);
			}
		}
	}


	/**
	 * Just checks if pid added is not the same as original pid and returns a boolean depending on that.
	 * @param originalPid the PIDs already registered in file.
	 * @param i the position of Profile IDs being added.
	 * @return true if they're the same, false otherwise.
	 */
	private static boolean checkIfExists(String[] originalPid, int[] profileID, int i) {
		for (int j = 0; j < originalPid.length; j++) {
			if (profileID[i] == Integer.parseInt(originalPid[j])) {
				return true;
			}
		}
		return false;
	}
}


