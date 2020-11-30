package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import source.labyrinth.*;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * LevelController acts as both a controller for the scene in which the game happens, and as a "Game Manager"
 * type of class for tracking the current game.
 * @author Fillip Serov
 */
public class LevelController implements Initializable {
	// The amount of "time" it takes for all players to complete one turn. (i.e. 3 players = 3)
	private static int timeForFullLoop;

	// The start of every players turn will add one to this. When we apply fire/ice we can set the
	// "unfreeze" time to be "currentTime + amount of players". Static so other classes can easily access it.
	private static int currentTime;

	private static String nextLevelToLoad; // Level file name
	private static String[] nextLevelProfiles; // The length of this

	@FXML private VBox boardContainer;
	@FXML private VBox leftVBox;
	@FXML private HBox bottomContainer;

	private Text[] playerActionAmountLabels = new Text[4];

	private Player[] players;
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

	/**
	 * Get the amount of time it takes for all players to complete a turn in this specific game, as this will
	 * change depending on the amount of players.
	 * @return int showing the time it takes for all players to do a one turn.
	 */
	public static int getTimeForFullLoop() {
		return timeForFullLoop;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LevelController");

		timeForFullLoop = nextLevelProfiles.length;

		silkBag = new SilkBag();
		currentPlayer = 0;
		tileRenderSize = 55;

		LevelData ld = LevelReader.readDataFile("source/resources/levels/" + nextLevelToLoad);

		//
		// Board Setup
		//
		System.out.println("Setting up board...");

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

		// TODO: Fill up SilkBag with Action Tiles somewhere here

		//
		// Player Setup
		//
		System.out.println("Setting up players...");

		players = new Player[nextLevelProfiles.length];
		for (int i = 0; i < nextLevelProfiles.length; i++) {
			Profile associatedProfile = null;
			if (nextLevelProfiles[i] != null) {
				associatedProfile = ProfileManager.getProfileByName(nextLevelProfiles[i]);
			}

			Player newPlayer = new Player(i, associatedProfile);

			int[] startingPosition = ld.getPlayerStartingPositions()[i];
			newPlayer.setStandingOn(this.board.getTileAt(startingPosition[0], startingPosition[1]));

			players[i] = newPlayer;
		}

		// Populating leftVBox with player info
		leftVBox.getChildren().clear();
		for (int i = 0; i < this.players.length; i++) {
			VBox playerVBox = new VBox();
			HBox playerNameAndIcon = new HBox();
			HBox playerActionTileAmount = new HBox();

			Circle playerIcon = new Circle(10);
			playerIcon.setFill(Player.getPlayerColor(i));

			Label playerLabel = new Label("Player " + (i + 1));
			if (this.players[i].getAssociatedProfile() != null) {
				playerLabel.setText(this.players[i].getAssociatedProfile().getName());
			}

			playerNameAndIcon.getChildren().addAll(playerIcon, playerLabel);

			Text actionText = new Text("0 Action Tiles");
			playerActionAmountLabels[i] = actionText;
			playerActionTileAmount.getChildren().add(actionText);

			playerNameAndIcon.setAlignment(Pos.BOTTOM_CENTER);
			playerActionTileAmount.setAlignment(Pos.TOP_CENTER);
			playerVBox.getChildren().addAll(playerNameAndIcon, playerActionTileAmount);
			playerVBox.setPrefHeight(200);

			leftVBox.getChildren().add(playerVBox);
		}

		// When everything is done, render the board for the first time
		boardContainer.getChildren().add(board.renderBoard((tileRenderSize)));
	}

	@FXML
	public void goToLevelMenu(ActionEvent event) {
		System.out.println("Going to level menu...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Level Select");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
