package source.labyrinth;

import java.util.HashMap;

/**
 * LevelData is created by LevelIO to store information about the level. This is needed as not
 * all information goes into a single Board class that can easily be returned.
 * @author Fillip Serov
 */
public class LevelData {
	private Board board;
	private int[][] playerStartingPositions;
	private HashMap<FloorTile.FloorType, Integer> floorTileAmounts;
	private HashMap<ActionTile.ActionType, Integer> actionTileAmounts;

	/**
	 * Create an instance of LevelData ready to set information
	 */
	public LevelData() {
		floorTileAmounts = new HashMap<>();
		actionTileAmounts = new HashMap<>();
	}

	/**
	 * @return Board instance that was generated
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @param board Board to store
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * @return Integer matrix representing player starting positions. i.e. [[0,0], [5,4]]
	 */
	public int[][] getPlayerStartingPositions() {
		return playerStartingPositions;
	}

	/**
	 * @param playerStartingPositions Integer matrix to use as starting positions
	 */
	public void setPlayerStartingPositions(int[][] playerStartingPositions) {
		this.playerStartingPositions = playerStartingPositions;
	}

	/**
	 * @param typeToChange FloorType to change
	 * @param amount Amount to change to
	 */
	public void setFloorTileAmount(FloorTile.FloorType typeToChange, int amount) {
		floorTileAmounts.put(typeToChange, amount);
	}

	/**
	 * @param typeToGet FloorType to return
	 * @return Amount of that FloorType
	 */
	public int getFloorTileAmount(FloorTile.FloorType typeToGet) {
		return floorTileAmounts.get(typeToGet);
	}

	/**
	 * @param typeToChange ActionType to change
	 * @param amount Amount to change to
	 */
	public void setActionTileAmount(ActionTile.ActionType typeToChange, int amount) {
		actionTileAmounts.put(typeToChange, amount);
	}

	/**
	 * @param typeToGet ActionType to return
	 * @return Amount of that ActionType
	 */
	public int getActionTileAmount(ActionTile.ActionType typeToGet) {
		return actionTileAmounts.get(typeToGet);
	}
}
