package source.labyrinth;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The Board class will store the layout and state of the current game.
 * @author Fillip Serov
 */
public class Board implements Serializable {
	private FloorTile[][] board;

	public Board(int width, int height) {
		this.board = new FloorTile[width][height];
	}

	/**
	 * @return Width of this Board.
	 */
	public int getWidth() {
		return this.board.length > 0 ? this.board.length : 0;
	}

	/**
	 * @return Height of this Board.
	 */
	public int getHeight() {
		return this.board.length > 0 ? this.board[0].length : 0;
	}

	/**
	 * Change the size of the board, either cutting off previous floor tiles or leaving empty spaces.
	 * If given invalid numbers (lower than 1), board will remain the same
	 * @param newWidth New width
	 * @param newHeight New height
	 */
	public void changeSize(int newWidth, int newHeight) {
		if (newWidth > 0 && newHeight > 0) {
			FloorTile[][] newBoard = new FloorTile[newWidth][newHeight];
			for (int x = 0; x < newWidth; x++) {
				for (int y = 0; y < newHeight; y++) {
					try {
						newBoard[x][y] = board[x][y];
					} catch (ArrayIndexOutOfBoundsException e) {
						break;
					}
				}
			}
			board = newBoard;
		}
	}

	/**
	 * Get a Boolean array representing which way a player can move from a certain tile.
	 * @param x X-position from where to calculate.
	 * @param y Y-position from where to calculate
	 * @return Boolean[] showing where movement is possible.
	 */
	public Boolean[] getMovableFrom(int x, int y) {
		Boolean[] toReturn = {false, false, false, false};

		Boolean[] atLocation = this.board[x][y].getMoveMask();

		// Check north
		if (y - 1 >= 0) {
			toReturn[0] = atLocation[0] && this.board[x][y-1].getNeighbourMoveMask()[2];
		}
		// Check east
		if (x + 1 < getWidth()) {
			toReturn[1] = atLocation[1] && this.board[x + 1][y].getNeighbourMoveMask()[3];
		}
		// Check south
		if (y + 1 < getHeight()) {
			toReturn[2] = atLocation[2] && this.board[x][y + 1].getNeighbourMoveMask()[0];
		}
		// Check west
		if (x - 1 >= 0) {
			toReturn[3] = atLocation[3] && this.board[x - 1][y].getNeighbourMoveMask()[1];
		}

		return toReturn;
	}

	/**
	 * Get two boolean arrays representing which rows/columns can be inserted into (no tiles in the way).
	 * The first array represents the columns, the second the rows. A value of true means insertion is allowed.
	 * @return Two boolean arrays in an array, first one representing columns and the second the rows.
	 */
	public Boolean[][] getInsertablePositions() {
		Boolean[][] toReturn = new Boolean[2][];
		toReturn[0] = new Boolean[getWidth()];
		toReturn[1] = new Boolean[getHeight()];
		Arrays.fill(toReturn[0], true);
		Arrays.fill(toReturn[1], true);

		// TODO: This is inefficient, once we know a row/column is fixed no other tiles there should be checked.
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (this.board[x][y].isCurrentlyFixed()) {
					// If a tile is fixed, set both the relevant column and row to false
					toReturn[0][x] = toReturn[1][y] = false;
				}
			}
		}

		return toReturn;
	}

	/**
	 * Insert a new tile into the board, based on direction and insertion point.
	 * @param newTile The FloorTile to insert
	 * @param insertionDirection Integer between 0-3 representing the 4 directions
	 * @param insertionPoint Where in the board to insert tile, starts at 0 up to width/height - 1
	 * @throws IllegalArgumentException if insertion is impossible
	 */
	public void insertFloorTile(FloorTile newTile, int insertionDirection, int insertionPoint)  throws IllegalArgumentException {
		// If the insertionDirection is 0 or 2, we are inserting into a column, 1 or 3, into a row
		boolean columnInsert = insertionDirection % 2 == 0;
		int inc = insertionDirection % 3 == 0 ? -1: 1;
		int start = insertionDirection % 3 == 0 ? (columnInsert ? getHeight() - 1: getWidth() - 1): 0;
		int fin = insertionDirection % 3 == 0 ? 0: (columnInsert ? getHeight() - 1: getWidth() - 1);

		// Quick error check
		if (insertionDirection < 0 || insertionDirection > 3) {
			throw new IllegalArgumentException("insertionDirection was out of bounds.");
		}
		if (insertionPoint < 0 || (columnInsert && insertionPoint > getWidth()) || (!columnInsert && insertionPoint > getHeight())) {
			throw new IllegalArgumentException("insertionPoint was out of bounds.");
		}


		if (columnInsert) {
			if (this.board[insertionPoint][start].getPlayer() != null) {
				this.board[insertionPoint][start].getPlayer().setStandingOn(newTile);
			}
			SilkBag.addTile(this.board[insertionPoint][start]);
			for (int i = start; i != fin; i += inc) {
				this.board[insertionPoint][i] = this.board[insertionPoint][i + inc];
			}
			this.board[insertionPoint][fin]=newTile;
		} else {
			if (this.board[start][insertionPoint].getPlayer() != null) {
				this.board[start][insertionPoint].getPlayer().setStandingOn(newTile);
			}
			SilkBag.addTile(this.board[start][insertionPoint]);
			for (int i = start; i != fin; i += inc) {
				this.board[i][insertionPoint] = this.board[i + inc][insertionPoint];
			}
			this.board[fin][insertionPoint]=newTile;
		}
	}

	/**
	 * @param x X-position
	 * @param y Y-position
	 * @return FloorTile if there is one, null otherwise.
	 */
	public FloorTile getTileAt(int x, int y) {
		return this.board[x][y] != null ? this.board[x][y] : null;
	}

	/**
	 * Specifically set a FloorTile at some position. This should only be used when setting up the board.
	 * @param tile The FloorTile to set it to.
	 * @param x X-position
	 * @param y Y-position
	 */
	public void setTileAt(FloorTile tile, int x, int y) {
		this.board[x][y] = tile;
	}

	public void setOnFire(int x, int y) {
		for (int i = x > 0 ? (x - 1) : 0; i < ((x < (getWidth() - 1))? (x + 2): getWidth()); i++) {
			for (int j = y > 0 ? (y - 1) : 0; j < ((y < (getHeight() - 1))? (y + 2): getHeight()); j++) {
				this.board[i][j].setOnFire();
			}
		}
	}

	public void setFreezeOn(int x, int y) {
		for (int i = x > 0 ? (x - 1) : 0; i < ((x < (getWidth() - 1))? (x + 2): getWidth()); i++) {
			for (int j = y > 0 ? (y - 1) : 0; j < ((y < (getHeight() - 1))? (y + 2): getHeight()); j++) {
				this.board[i][j].freeze();
			}
		}
	}

	public boolean canSetOnFire(int x, int y) {
		boolean canSet = true;
		for (int i = x > 0 ? (x - 1) : 0; i < ((x < (getWidth() - 1))? (x + 2): getWidth()); i++) {
			for (int j = y > 0 ? (y - 1) : 0; j < ((y < (getHeight() - 1))? (y + 2): getHeight()); j++) {
				canSet= (this.board[i][j].getPlayer() == null) && canSet;
			}
		}
		return canSet;
	}

}