package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

	private enum TurnPhases {
		DRAWING,
		PLACEMENT,
		PLAYACTION,
		MOVEMENT
	}

	private VBox[] playerSubInfoVBoxes;
	private Player[] players;
	private int currentPlayer; // 0 to 3, player that is doing their turn
	private Board board;
	private GridPane renderedBoard;
	private int tileRenderSize; // Changed by zoom in/zoom out buttons
	private FloorTile floorTileToInsert;
	private TurnPhases currentTurnPhase;
	private ActionTile.ActionType usedAction; // We "used" this action, and are now applying it

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

		// Create all the action tiles and add them to the Board.
		// TODO: Use a HashMap in LevelData so we can just do all of this in one loop
		for (int i = 0; i < ld.getFireAmount(); i++) {
			SilkBag.addTile(new ActionTile(ActionTile.ActionType.FIRE));
		}
		for (int i = 0; i < ld.getIceAmount(); i++) {
			SilkBag.addTile(new ActionTile(ActionTile.ActionType.ICE));
		}
		for (int i = 0; i < ld.getBacktrackAmount(); i++) {
			SilkBag.addTile(new ActionTile(ActionTile.ActionType.BACKTRACK));
		}
		for (int i = 0; i < ld.getDoubleAmount(); i++) {
			SilkBag.addTile(new ActionTile(ActionTile.ActionType.DOUBLEMOVE));
		}

		//
		// Player Setup
		//
		System.out.println("Setting up players...");

		players = new Player[nextLevelProfiles.length];
		playerSubInfoVBoxes = new VBox[players.length];
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
		drawingPhase();
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

	private void drawingPhase() {
		renderBoard();
		currentTurnPhase = TurnPhases.DRAWING;
		updateSubInfoVBoxes();
		bottomContainer.getChildren().clear();

		Button drawButton = new Button("Draw a tile from the silk bag to start your turn");
		drawButton.setOnMouseClicked(event -> {
			Tile received = SilkBag.getRandomTile();
			if (received instanceof FloorTile) {
				System.out.println("Player " + currentPlayer + " drew " + ((FloorTile) received).exportSelf());
				placementPhase((FloorTile) received);
			} else {
				ActionTile thisAction = (ActionTile) received;

				// Add 0.5f to the amount the player has (of this action). When we check how many we have in the
				// PlayAction phase, we will round down. At the end of the turn, any actions that have a hanging
				// 0.5f will get rounded up. Because of this we don't have to store instances of ActionTiles.
				players[currentPlayer].setActionAmount(thisAction.getType(), players[currentPlayer].getActionAmount(thisAction.getType()) + 0.5f);
				System.out.println("Player " + currentPlayer + " drew " + thisAction.getType().toString());

				playActionPhase();
			}
		});

		bottomContainer.getChildren().add(drawButton);
	}

	private void placementPhase(FloorTile nextFloorTileToInsert) {
		currentTurnPhase = TurnPhases.PLACEMENT;
		this.floorTileToInsert = nextFloorTileToInsert;
		renderPlacementMenu();
		renderBoard();
	}

	private void renderPlacementMenu() {
		bottomContainer.getChildren().clear();
		bottomContainer.getChildren().add(floorTileToInsert.renderTile(tileRenderSize));
	}

	private void endPlacementPhase(int insertionDirection, int insertionPoint) {
		this.board.insertFloorTile(this.floorTileToInsert, insertionDirection, insertionPoint);
		this.floorTileToInsert = null;
		renderBoard();
		playActionPhase();
	}

	private void playActionPhase() {
		currentTurnPhase = TurnPhases.PLAYACTION;
		renderActionMenu();
	}

	/**
	 * During the PlayAction phase, we call renderActionMenu to show what Action Tiles we can use (and how
	 * many we have) in the bottom container.
	 */
	private void renderActionMenu() {
		bottomContainer.getChildren().clear();

		// If this player has ActionTiles, show them in the bottom container
		for (ActionTile.ActionType at : ActionTile.ActionType.values()) {
			ImageView iv = new ImageView(new Image(at.imageURL, 70, 70, false, false));
			StackPane stack = new StackPane(iv);
			stack.setAlignment(Pos.TOP_LEFT);

			// When we re-render we display the currently chosen action
			if (at == usedAction){
				ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png",70,70,false,false));
				chosen.setOpacity(0.5);
				stack.getChildren().addAll(chosen);
			}

			// This will always down cast, so no Math.Floor needed (3.99f -> 4)
			System.out.println(players[currentPlayer].getActionAmount(at));
			int thisAmount = (int) players[currentPlayer].getActionAmount(at);

			Text numOfTiles = new Text(thisAmount + " available");
			numOfTiles.setStyle("-fx-font-weight: bold");
			numOfTiles.setFill(players[currentPlayer].getActionAmount(at) < 1 ? Color.RED : Color.GREEN);
			stack.getChildren().add(numOfTiles);

			// When we click this action
			stack.setOnMouseClicked(event -> {
				this.usedAction = at;
				renderActionMenu();
				renderBoard();
				handleClickATChoice();
			});
			bottomContainer.getChildren().add(stack);
		}

		// We don't have to use an Action (even if available), so add a button to just skip to the movement phase
		Button skipButton = new Button("Skip");
		skipButton.setPrefSize(70, 70);
		skipButton.setOnMouseClicked(event -> {
			movementPhase();
		});
		bottomContainer.getChildren().add(skipButton);
	}

	private void movementPhase() {
		renderBoard();
		usedAction = null;
		currentTurnPhase = TurnPhases.MOVEMENT;
		int[] playerPos = getPlayerXYPosition(currentPlayer);
		bottomContainer.getChildren().clear();
		showWay();
		// TEMP
		//endTurn();
	}

	private void endTurn() {
		// If a tile amount has a decimal (*.5), then they received one of those action tiles this turn,
		// so we bump it up so that it is fully usable next turn.
		Player endingPlayer = players[currentPlayer];
		for (ActionTile.ActionType at : ActionTile.ActionType.values()) {
			if (endingPlayer.getActionAmount(at) % 1.0f != 0) {
				endingPlayer.setActionAmount(at, (float) Math.ceil(endingPlayer.getActionAmount(at)));
			}
		}

		// Go up by one or rotate back to 0
		currentPlayer = (currentPlayer < players.length - 1) ? currentPlayer + 1 : 0;
		currentTime++;

		drawingPhase();
	}

	/**
	 * A dirty hacky method to very slowly find a Player somewhere in a Board. TODO: Replace
	 * @param playerID
	 * @return
	 */
	private int[] getPlayerXYPosition(int playerID) {
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				FloorTile ft = this.board.getTileAt(x, y);
				if (ft.getPlayer() != null && ft.getPlayer().getIdInGame() == playerID) {
					return new int[] {x, y};
				}
			}
		}
		return null;
	}

	private void handleClickATChoice() {
		System.out.println(usedAction);

		if (usedAction == ActionTile.ActionType.DOUBLEMOVE) {
			showWay();
		}

	}

	private void showWay() {
		int[] pos = getPlayerXYPosition(currentPlayer);
		Boolean[] moveMask = board.getMovableFrom(pos[0],pos[1]);
		boolean isThereAWay = false;
		/*
		 runs setAsWay on neighbours if they exist and player can move there
		 uses index in moveMask to calculate neighbour's coordinate
		*/
		for (int i = 0; i < moveMask.length; i++) {
			if (moveMask[i]) {
				isThereAWay = true;
				if (i % 2 == 0) {
					setAsWay(pos[0],(pos[1]-1+i));
				} else {
					setAsWay((pos[0]+2-i),pos[1]);
				}
			}
		}

		if (!isThereAWay) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Unfortunately you have no available moves. You will remain where you are.");
			alert.showAndWait();
			if (currentTurnPhase == TurnPhases.MOVEMENT) {
				endTurn();
			}
		}
	}

	private void setAsWay(int x, int y) {
		ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png",tileRenderSize,tileRenderSize,false,false));
		chosen.setOpacity(0.5);
		StackPane wayTile = getStackPaneTileByXY(x,y);
		wayTile.getChildren().add(chosen);
		wayTile.setOnMouseClicked(event -> {
			move(x,y);
		});
	}

	private void move(Player player,int x,int y) {
		player.setStandingOn(board.getTileAt(x,y));

		switch (currentTurnPhase) {
			case PLAYACTION:
				// If we were moving in the PLAYACTION phase, we just used a DOUBLEMOVE
				player.addToPastPositions(x, y);
				movementPhase();
				break;
			case MOVEMENT:
				player.addToPastPositions(x, y);
				endTurn();
				break;
		}
	}

	private void move(int x,int y) {
		move(players[currentPlayer],x,y);
	}

	private StackPane getStackPaneTileByXY(int col, int row) {
		for (Node node : renderedBoard.getChildren()) {
			if (GridPane.getColumnIndex(node) == col+1 && GridPane.getRowIndex(node) == row+1) {
				return (StackPane) node;
			}
		}
		return null;
	}

	private void handleActionClickOn(int x, int y) {
		switch (usedAction) {
			case FIRE:
				// Fire will only apply and move the turn phase forward if it is able to be applied.
				if (board.canSetOnFire(x, y)) {
					board.setOnFire(x, y);
					movementPhase();
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Cannot apply fire here, there is a player nearby (3 x 3 area)");
					alert.showAndWait();
				}
				break;
			case ICE:
				board.setFreezeOn(x, y);
				movementPhase();
				break;
			case BACKTRACK:
				FloorTile thisTile = this.board.getTileAt(x, y);
				if (thisTile.getPlayer() != null || !(thisTile.getPlayer().getHasBeenBacktracked())) {
					thisTile.getPlayer().setHasBeenBacktracked(true);
					// TODO: Perform a backtrack on the clicked player
					movementPhase();
				}
				break;
			case DOUBLEMOVE:
				System.out.println("doublemmove");

				break;
		}
	}

	private void handleFloorTileClickAt(int x, int y) {
		if (currentTurnPhase == TurnPhases.PLAYACTION && usedAction != null) {
			handleActionClickOn(x, y);
		}

		System.out.println("This tile's mask is " + Arrays.toString(this.board.getTileAt(x, y).getMoveMask()));
		System.out.println("From this tile you can move to " + Arrays.toString(this.board.getMovableFrom(x, y)));
	}

	private void renderBoard() {
		// Clear the old render
		boardContainer.getChildren().clear();

		renderedBoard = new GridPane();
		renderedBoard.setAlignment(Pos.CENTER);

		Boolean[][] insertableMask = this.board.getInsertablePositions();
		Image insertionImage = new Image("source/resources/img/insert_arrow.png", tileRenderSize, tileRenderSize, false, false);

		// If we are in the placement phase (i.e. we have a FloorTile), show some additional buttons
		if (floorTileToInsert != null) {
			// TODO: Game will soft lock if there are no rows/columns that can be inserted into

			// Put column buttons (start at 1 since 0,0 is the empty top left spot)
			for (int x = 1; x <= this.board.getWidth(); x++) {
				if (insertableMask[0][x - 1]) {
					int finalX = x - 1;

					ImageView topOfColumn = new ImageView(insertionImage);
					topOfColumn.setRotate(180);
					topOfColumn.setOnMouseClicked(event -> {
						System.out.println("Inserting at direction " + "0" + " at insertion point " + finalX);
						endPlacementPhase(0, finalX);
					});
					renderedBoard.add(topOfColumn, x, 0);

					ImageView bottomOfColumn = new ImageView(insertionImage);
					bottomOfColumn.setOnMouseClicked(event -> {
						System.out.println("Inserting at direction " + "2" + " at insertion point " + finalX);
						endPlacementPhase(2, finalX);
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
						endPlacementPhase(3, finalY);
					});
					renderedBoard.add(leftRow, 0, y);

					ImageView rightRow = new ImageView(insertionImage);
					rightRow.setRotate(-90);
					rightRow.setOnMouseClicked(event -> {
						System.out.println("Inserting at direction " + "1" + " at insertion point " + finalY);
						endPlacementPhase(1, finalY);
					});
					renderedBoard.add(rightRow, this.board.getWidth() + 1, y);
				}
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
					handleFloorTileClickAt(finalX, finalY);
				});

				renderedBoard.add(stack, x + 1, y + 1);
			}
		}

		// renderedBoard.setGridLinesVisible(true);

		boardContainer.getChildren().add(renderedBoard);
	}

	private void updateSubInfoVBoxes() {
		for (int i = 0; i < playerSubInfoVBoxes.length; i++) {
			playerSubInfoVBoxes[i].getChildren().clear();
			playerSubInfoVBoxes[i].getChildren().add(new Text("0 Action Tiles"));
			if (i == currentPlayer) {
				Text yourTurn = new Text("Your Turn");
				yourTurn.setFill(Color.GREEN);
				yourTurn.setStyle("-fx-font-weight: bold");
				playerSubInfoVBoxes[i].getChildren().add(yourTurn);
			}
		}
	}

	/**
	 * @param playerID ID for a player in this game, 0-3
	 * @return A VBox containing information about the player (Their color, profile name if they have one, and action
	 * tile amount).
	 */
	private VBox createPlayerInfoVBox(int playerID) {
		VBox playerVBox = new VBox();
		HBox playerNameAndIcon = new HBox();
		VBox playerSubInfoHBox = new VBox();

		playerSubInfoVBoxes[playerID] = playerSubInfoHBox;

		Circle playerIcon = new Circle(10);
		playerIcon.setFill(Player.getPlayerColor(playerID));

		Label playerLabel = new Label("Player " + (playerID + 1));
		if (this.players[playerID].getAssociatedProfile() != null) {
			playerLabel.setText(this.players[playerID].getAssociatedProfile().getName());
		}

		playerNameAndIcon.getChildren().addAll(playerIcon, playerLabel);

		playerNameAndIcon.setAlignment(Pos.BOTTOM_CENTER);
		playerSubInfoHBox.setAlignment(Pos.TOP_CENTER);
		playerVBox.getChildren().addAll(playerNameAndIcon, playerSubInfoHBox);
		playerVBox.setPrefHeight(200);

		return playerVBox;
	}
}
