package source.labyrinth;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
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
		this.board[0][2].setFixed(true);
		this.board[2][0].setFixed(true);
		this.board[6][0].setFixed(true);
		this.board[0][6].setFixed(true);
	}

	/**
	 * Get a JavaFX GridPane representing the board in it's current state.
	 * @return GridPane representing the Board
	 */
	public GridPane renderBoard() {
		GridPane gameBoard = new GridPane();
		gameBoard.setPrefSize(800, 800);

		int TILE_SIZE = 55;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				FloorTile current = this.board[x][y];

				// TODO: This looks terrible, find the proper way of doing it.
				Image img = new Image(String.valueOf(getClass().getResource(current.getImageURL())), TILE_SIZE, TILE_SIZE, false, false);

				ImageView iv = new ImageView(img);
				iv.setRotate(90 * current.getOrientation());

				Text text = new Text("(" + x + ", " + y + ")");
				text.setFont(Font.font(15));

				StackPane stack = new StackPane(iv, text);

				// Show indicator that tile is fixed
				if (current.getFixed()) {
					Image fixedImage = new Image(String.valueOf(getClass().getResource("../resources/img/fixed_marker.png")), TILE_SIZE, TILE_SIZE, false, false);
					ImageView fixedImageView = new ImageView(fixedImage);
					fixedImageView.setOpacity(0.5);
					stack.getChildren().addAll(fixedImageView);
				}

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
}