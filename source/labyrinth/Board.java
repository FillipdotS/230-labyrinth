package source.labyrinth;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Random;

/**
 * The Board class will store the layout of the current level.
 * @author Fillip Serov
 */
public class Board {
	private int width;
	private int height;
	private FloorTile[][] board;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;

		this.board = new FloorTile[width][height];

		// temp code, just fill up the board with random things to test it out
		for (int x = 0; x < this.board.length; x++) {
			for (int y = 0; y < this.board[0].length; y++) {
				int randomOrientation = new Random().nextInt(5);
				int randomTile = new Random().nextInt(4);

				FloorTile toPlace;
				switch (randomTile) {
					case 0:
						toPlace = new FloorTile(randomOrientation, FloorTile.TileType.STRAIGHT);
						break;
					case 1:
						toPlace = new FloorTile(randomOrientation, FloorTile.TileType.TSHAPE);
						break;
					case 2:
						toPlace = new FloorTile(randomOrientation, FloorTile.TileType.GOAL);
						break;
					default:
						toPlace = new FloorTile(randomOrientation, FloorTile.TileType.CORNER);
				}


				this.board[x][y] = toPlace;
			}
		}
		this.board[0][0].setFixed(true);
	}

	/**
	 * Get a JavaFX GridPane representing the board in it's current state.
	 * @param tileRenderSize Size at which each tile in the board will be rendered at.
	 * @return GridPane representing the Board
	 */
	public GridPane renderBoard(int tileRenderSize) {
		GridPane gameBoard = new GridPane();
		//gameBoard.setPrefSize(800, 800);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				FloorTile current = this.board[x][y];
				StackPane stack = current.renderTile(tileRenderSize);
				stack.getChildren().add(new Text("(" + x + ", " + y + ")"));

				int finalX = x;
				int finalY = y;
				stack.setOnMouseClicked(event -> {
					System.out.println("This tile's mask is " + Arrays.toString(this.board[finalX][finalY].getMoveMask()));
					System.out.println("From this tile you can move to " + Arrays.toString(getMovableFrom(finalX, finalY)));
				});

				gameBoard.add(stack, x, y);
			}
		}

		gameBoard.setGridLinesVisible(true);

		return gameBoard;
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

		// TODO: Maybe make these ifs into something cleaner?

		// Check north
		if (y - 1 >= 0) {
			toReturn[0] = atLocation[0] && this.board[x][y-1].getMoveMask()[2];
		}
		// Check east
		if (x + 1 < this.width) {
			toReturn[1] = atLocation[1] && this.board[x + 1][y].getMoveMask()[3];
		}
		// Check south
		if (y + 1 < height) {
			toReturn[2] = atLocation[2] && this.board[x][y + 1].getMoveMask()[0];
		}
		// Check west
		if (x - 1 >= 0) {
			toReturn[3] = atLocation[3] && this.board[x - 1][y].getMoveMask()[1];
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
		toReturn[0] = new Boolean[width];
		toReturn[1] = new Boolean[height];
		Arrays.fill(toReturn[0], true);
		Arrays.fill(toReturn[1], true);

		// TODO: This is inefficient, once we know a row/column is fixed no other tiles there should be checked.
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (this.board[x][y].getFixed()) {
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
	 * @throws IllegalArgumentException
	 */
	public void insertFloorTile(FloorTile newTile, int insertionDirection, int insertionPoint)  throws IllegalArgumentException {
		// If the insertionDirection is 0 or 2, we are inserting into a column, 1 or 3, into a row
		Boolean columnInsert = insertionDirection % 2 == 0;
		int inc = insertionDirection % 3 == 0 ? -1: 1;
		int start = insertionDirection % 3 == 0 ? (columnInsert ? this.height - 1: this.width - 1): 0;
		int fin = insertionDirection % 3 == 0 ? 0: (columnInsert ? this.height - 1: this.width - 1);

		// Quick error check
		if (insertionDirection < 0 || insertionDirection > 3) {
			throw new IllegalArgumentException("insertionDirection was out of bounds.");
		}
		if (insertionPoint < 0 || (columnInsert && insertionPoint > this.width) || (!columnInsert && insertionPoint > this.height)) {
			throw new IllegalArgumentException("insertionPoint was out of bounds.");
		}

		// TODO: Give the tiles back to the silk bag before the start of every loop
		if (columnInsert) {
			for (int i = start; i != fin; i += inc) {
				this.board[insertionPoint][i] = this.board[insertionPoint][i + inc];
			}
			this.board[insertionPoint][fin]=newTile;
		} else {
			for (int i = start; i != fin; i += inc) {
				this.board[i][insertionPoint] = this.board[i + inc][insertionPoint];
			}
			this.board[fin][insertionPoint]=newTile;
		}
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

	/**
	 * Get a string (with newlines) that represents the current state of the board. The first line will contain
	 * two numbers representing the width and height of the board. The rest of the lines are tile-specific.
	 * @return A large string (with newlines) that represents the current state of the board.
	 */
	public String exportSelf() {
		String toReturn = this.width + "," + this.height;

		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				toReturn += System.lineSeparator();
				toReturn += this.board[x][y].exportSelf();
			}
		}

		return toReturn;
	}

	public void setOnFire(int x, int y) {
		for (int i = x > 0 ? (x - 1) : 0; i < ((x < (this.width - 1))? (x + 2): this.width); i++) {
			for (int j = y > 0 ? (y - 1) : 0; j < ((y < (this.height - 1))? (y + 2): this.height); j++) {
				System.out.println(i+" "+j);
			}
		}
	}
}