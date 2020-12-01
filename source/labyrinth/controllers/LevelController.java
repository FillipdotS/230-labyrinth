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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import source.labyrinth.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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
	private int currentPlayer; // 0 to 3, player that is doing their turn
	private Board board;
	private int tileRenderSize; // Changed by zoom in/zoom out buttons
	private FloorTile floorTileToInsert = new FloorTile(0, FloorTile.TileType.STRAIGHT);

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LevelController");

		timeForFullLoop = nextLevelProfiles.length;

		currentPlayer = 0;
		tileRenderSize = 55;

		LevelData ld = LevelReader.readDataFile("source/resources/levels/" + nextLevelToLoad);

		//
		// Board Setup
		//
		System.out.println("Setting up board...");

		board = ld.getBoard();
		boardContainer.setPrefHeight((board.getHeight() * tileRenderSize) + (2 * tileRenderSize));
		boardContainer.setPrefWidth((board.getWidth() * tileRenderSize) + (2 * tileRenderSize));

		// Create all the floor tiles and add them to the silk bag.
		for (int i = 0; i < ld.getStraightAmount(); i++) {
			SilkBag.addTile(new FloorTile(new Random().nextInt(5), FloorTile.TileType.STRAIGHT));
		}
		for (int i = 0; i < ld.getCornerAmount(); i++) {
			SilkBag.addTile(new FloorTile(new Random().nextInt(5), FloorTile.TileType.CORNER));
		}
		for (int i = 0; i < ld.getTshapeAmount(); i++) {
			SilkBag.addTile(new FloorTile(new Random().nextInt(5), FloorTile.TileType.TSHAPE));
		}

		// IMPORTANT: Before we create and add action tiles to the bag, we use the silk bag to fill up
		// the board with random tiles (since we know only floor tiles are stored in the bag right now).
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				if (this.board.getTileAt(x, y) == null) {
					this.board.setTileAt((FloorTile)SilkBag.getRandomTile(), x, y);
				}
			}
		}

		// TODO: Fill up SilkBag with Action Tiles somewhere here

		//
		// Player Setup
		//
		System.out.println("Setting up players...");

		players = new Player[nextLevelProfiles.length];
		for (int i = 0; i < players.length; i++) {
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
			leftVBox.getChildren().add(createPlayerInfoVBox(i));
		}

		// When everything is done, render the board for the first time
		boardContainer.getChildren().add(renderBoard());
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

	private GridPane renderBoard() {
		// Clear the old render
		boardContainer.getChildren().clear();

		GridPane renderedBoard = new GridPane();
		renderedBoard.setAlignment(Pos.CENTER);

		Boolean[][] insertableMask = this.board.getInsertablePositions();
		Image insertionImage = new Image("source/resources/img/insert_arrow.png", tileRenderSize, tileRenderSize, false, false);

		// Put column buttons (start at 1 since 0,0 is the empty top left spot)
		for (int x = 1; x <= this.board.getWidth(); x++) {
			if (insertableMask[0][x - 1]) {
				int finalX = x - 1;

				ImageView topOfColumn = new ImageView(insertionImage);
				topOfColumn.setRotate(180);
				topOfColumn.setOnMouseClicked(event -> {
					System.out.println("Inserting at direction " + "0" + " at insertion point " + finalX);
					this.board.insertFloorTile(floorTileToInsert, 0, finalX);
					boardContainer.getChildren().add(renderBoard());
				});
				renderedBoard.add(topOfColumn, x, 0);

				ImageView bottomOfColumn = new ImageView(insertionImage);
				bottomOfColumn.setOnMouseClicked(event -> {
					System.out.println("Inserting at direction " + "2" + " at insertion point " + finalX);
					this.board.insertFloorTile(floorTileToInsert, 2, finalX);
					boardContainer.getChildren().add(renderBoard());
				});
				renderedBoard.add(bottomOfColumn, x, this.board.getHeight() + 1);
			}
		}

		// Put row buttons (start at 1)
		for (int y = 1; y <= this.board.getHeight(); y++) {
			if (insertableMask[1][y - 1]) {
				int finalY = y - 1;

				ImageView leftRow = new ImageView(insertionImage);
				leftRow.setRotate(90);
				leftRow.setOnMouseClicked(event -> {
					System.out.println("Inserting at direction " + "3" + " at insertion point " + finalY);
					this.board.insertFloorTile(floorTileToInsert, 3, finalY);
					boardContainer.getChildren().add(renderBoard());
				});
				renderedBoard.add(leftRow, 0, y);

				ImageView rightRow = new ImageView(insertionImage);
				rightRow.setRotate(-90);
				rightRow.setOnMouseClicked(event -> {
					System.out.println("Inserting at direction " + "1" + " at insertion point " + finalY);
					this.board.insertFloorTile(floorTileToInsert, 1, finalY);
					boardContainer.getChildren().add(renderBoard());
				});
				renderedBoard.add(rightRow, this.board.getWidth() + 1,  y);
			}
		}

		// The actual board render
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				FloorTile current = this.board.getTileAt(x, y);
				StackPane stack = current.renderTile(tileRenderSize);
				stack.getChildren().add(new Text("(" + x + ", " + y + ")"));

				int finalX = x;
				int finalY = y;
				stack.setOnMouseClicked(event -> {
					System.out.println("This tile's mask is " + Arrays.toString(this.board.getTileAt(finalX, finalY).getMoveMask()));
					System.out.println("From this tile you can move to " + Arrays.toString(this.board.getMovableFrom(finalX, finalY)));
				});

				renderedBoard.add(stack, x + 1, y + 1);
			}
		}

		// renderedBoard.setGridLinesVisible(true);

		return renderedBoard;
	}

	/**
	 * @param playerID ID for a player in this game, 0-3
	 * @return A VBox containing information about the player (Their color, profile name if they have one, and action
	 * tile amount).
	 */
	private VBox createPlayerInfoVBox(int playerID) {
		VBox playerVBox = new VBox();
		HBox playerNameAndIcon = new HBox();
		HBox playerActionTileAmount = new HBox();

		Circle playerIcon = new Circle(10);
		playerIcon.setFill(Player.getPlayerColor(playerID));

		Label playerLabel = new Label("Player " + (playerID + 1));
		if (this.players[playerID].getAssociatedProfile() != null) {
			playerLabel.setText(this.players[playerID].getAssociatedProfile().getName());
		}

		playerNameAndIcon.getChildren().addAll(playerIcon, playerLabel);

		Text actionText = new Text("0 Action Tiles");
		playerActionAmountLabels[playerID] = actionText;
		playerActionTileAmount.getChildren().add(actionText);

		playerNameAndIcon.setAlignment(Pos.BOTTOM_CENTER);
		playerActionTileAmount.setAlignment(Pos.TOP_CENTER);
		playerVBox.getChildren().addAll(playerNameAndIcon, playerActionTileAmount);
		playerVBox.setPrefHeight(200);

		return playerVBox;
	}
}
