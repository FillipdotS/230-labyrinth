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
	//test Narcis 
	private FloorTile[][] board;

	public Board(int width, int height) {
		this.width = width;
		this.height = height;

		this.board = new FloorTile[width][height];

		// temp code, just fill up the board with random things to test it out
		for (int x = 0; x < this.board.length; x++) {
			for (int y = 0; y < this.board[0].length; y++) {
				int randomOrientation = new Random().nextInt(5);
				int randomTile = new Random().nextInt(3);

				FloorTile toPlace;
				switch (randomTile) {
					case 0:
						toPlace = new Straight(randomOrientation);
						break;
					case 1:
						toPlace = new TShape(randomOrientation);
						break;
					default:
						toPlace = new Corner(randomOrientation);
				}

				this.board[x][y] = toPlace;
			}
		}
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
				// TODO: This looks terrible, find the proper way of doing it.
				Image img = new Image(String.valueOf(getClass().getResource(this.board[x][y].imageURL)), TILE_SIZE, TILE_SIZE, false, false);

				ImageView iv = new ImageView(img);
				iv.setRotate(90 * this.board[x][y].orientation);
				//iv.setFitHeight(50);
				//iv.setFitWidth(50);

				Text text = new Text("(" + x + ", " + y + ")");
				text.setFont(Font.font(15));

				StackPane stack = new StackPane(iv, text);
				//cord
				int finalX = x;
				int finalY = y;
				stack.setOnMouseClicked(event -> {
					System.out.println("This tile's mask is " + Arrays.toString(this.board[finalX][finalY].moveMask));
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
	}		//Does this work??!!
}