package source.labyrinth.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import source.labyrinth.*;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * LevelController acts as both a controller for the scene in which the game happens, and as a "Game Manager"
 * type of class for tracking the current game.
 * @author Fillip Serov
 */
public class LevelController implements Initializable {
	// The start of every players turn will add one to this. When we apply fire/ice we can set the
	// "unfreeze" time to be "currentTime + amount of players". Static so other classes can easily access it.
	private static int currentTime;

	private static String nextLevelToLoad; // Level file name
	private static String[] nextLevelProfiles; // The length of this

	@FXML private VBox boardContainer;
	@FXML private VBox leftVBox;
	@FXML private HBox bottomContainer;

	private Text[] playerActionAmountLabels = new Text[4];

	// private Player[] players;
	private SilkBag silkBag;
	private int currentPlayer; // 0 to 3, player that is doing their turn
	private Board board;
	private int tileRenderSize; // Changed by zoom in/zoom out buttons

	/**
	 * Get the current game time as an int. Will always be >0.
	 * @return int representing the game time.
	 */
	public static int getCurrentTime() {
		return currentTime;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LevelController");

		silkBag = new SilkBag();
		currentPlayer = 0;
		tileRenderSize = 55;

		LevelData ld = LevelReader.readDataFile("source/resources/levels/example_level.txt");

		board = ld.getBoard();
		boardContainer.setPrefHeight(board.getHeight() * tileRenderSize);
		boardContainer.setPrefWidth(board.getWidth() * tileRenderSize);

		// Create all the floor tiles and add them to the silk bag.
		for (int i = 0; i < ld.getStraightAmount(); i++) {
			silkBag.returnTile(new FloorTile(new Random().nextInt(5), FloorTile.TileType.STRAIGHT));
		}
		for (int i = 0; i < ld.getCornerAmount(); i++) {
			silkBag.returnTile(new FloorTile(new Random().nextInt(5), FloorTile.TileType.CORNER));
		}
		for (int i = 0; i < ld.getTshapeAmount(); i++) {
			silkBag.returnTile(new FloorTile(new Random().nextInt(5), FloorTile.TileType.TSHAPE));
		}

		// IMPORTANT: Before we create and add action tiles to the bag, we use the silk bag to fill up
		// the board with random tiles (since we know only floor tiles are stored in the bag right now).
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				if (this.board.getTileAt(x, y) == null) {
					this.board.setTileAt((FloorTile)silkBag.getRandomTile(), x, y);
				}
			}
		}

		// Populating leftVBox with player info
		leftVBox.getChildren().clear();
		for (int i = 0; i < 4; i++) {
			VBox playerVBox = new VBox();
			HBox playerNameAndIcon = new HBox();
			HBox playerActionTileAmount = new HBox();

			Color playerColor;

			switch (i) {
				case 0:
					playerColor = Color.BLUE;
					break;
				case 1:
					playerColor = Color.RED;
					break;
				case 2:
					playerColor = Color.GREEN;
					break;
				case 3:
					playerColor = Color.PURPLE;
					break;
				default:
					playerColor = Color.GREY;
			}

			Circle playerIcon = new Circle(10);
			playerIcon.setFill(playerColor);

			// TODO: Change this to profile name if it exists for this player
			Label playerName = new Label("Player " + (i + 1));

			playerNameAndIcon.getChildren().addAll(playerIcon, playerName);

			// TODO: Change to actually depend on the amount of action tiles that a player has
			Text actionText = new Text("0 Action Tiles");
			playerActionAmountLabels[i] = actionText;
			playerActionTileAmount.getChildren().add(actionText);

			playerNameAndIcon.setAlignment(Pos.BOTTOM_CENTER);
			playerActionTileAmount.setAlignment(Pos.TOP_CENTER);
			playerVBox.getChildren().addAll(playerNameAndIcon, playerActionTileAmount);
			playerVBox.setPrefHeight(200);

			leftVBox.getChildren().add(playerVBox);
		}

		boardContainer.getChildren().add(board.renderBoard((tileRenderSize)));
	}

	/**
	 * exportToSave will collect all necessary information about the game and save it to a file. An alert
	 * will popup to show the save name.
	 */
	public void exportToSave() {
		// TODO: Actually implement it
	}

	/**
	 * Next time the level scene is loaded, it will build from this level file.
	 * @param nextLevelToLoad Level Name
	 */
	public static void setNextLevelToLoad(String nextLevelToLoad) {
		LevelController.nextLevelToLoad = nextLevelToLoad;
	}

	/**
	 * Next time the level scene is loaded, it will use these profiles. The length of this array is also
	 * the amount of players to use.
	 * @param nextLevelProfiles Profiles (given as strings) to use in this game.
	 */
	public static void setNextLevelProfiles(String[] nextLevelProfiles) {
		LevelController.nextLevelProfiles = nextLevelProfiles;
	}
}
