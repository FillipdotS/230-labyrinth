package source.labyrinth;

/**
 * LevelData is created by LevelReader to store information about the level. This is needed as not
 * all information goes into a single Board class that can easily be returned.
 * @author Fillip Serov
 */
public class LevelData {
	private Board board;
	private int[][] playerStartingPositions;

	// FloorTiles
	private int straightAmount;
	private int tshapeAmount;
	private int cornerAmount;

	// ActionTiles
	private int fireAmount;
	private int iceAmount;
	private int doubleAmount;
	private int backtrackAmount;

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public int[][] getPlayerStartingPositions() {
		return playerStartingPositions;
	}

	public void setPlayerStartingPositions(int[][] playerStartingPositions) {
		this.playerStartingPositions = playerStartingPositions;
	}

	public int getStraightAmount() {
		return straightAmount;
	}

	public void setStraightAmount(int straightAmount) {
		this.straightAmount = straightAmount;
	}

	public int getTshapeAmount() {
		return tshapeAmount;
	}

	public void setTshapeAmount(int tshapeAmount) {
		this.tshapeAmount = tshapeAmount;
	}

	public int getCornerAmount() {
		return cornerAmount;
	}

	public void setCornerAmount(int cornerAmount) {
		this.cornerAmount = cornerAmount;
	}

	public int getFireAmount() {
		return fireAmount;
	}

	public void setFireAmount(int fireAmount) {
		this.fireAmount = fireAmount;
	}

	public int getIceAmount() {
		return iceAmount;
	}

	public void setIceAmount(int iceAmount) {
		this.iceAmount = iceAmount;
	}

	public int getDoubleAmount() {
		return doubleAmount;
	}

	public void setDoubleAmount(int doubleAmount) {
		this.doubleAmount = doubleAmount;
	}

	public int getBacktrackAmount() {
		return backtrackAmount;
	}

	public void setBacktrackAmount(int backtrackAmount) {
		this.backtrackAmount = backtrackAmount;
	}
}
