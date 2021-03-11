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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import source.labyrinth.*;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * LevelController acts as both a controller for the scene in which the game happens, and as a "Game Manager"
 * type of class for tracking the current game.
 *
 * @author Fillip Serov, Erik Miller
 */
public class LevelController implements Initializable {
	// Changed by zoom in/zoom out buttons.
	private static int tileRenderSize = 64;

	// The amount of "time" it takes for all players to complete one turn. (i.e. 3 players = 3)
	private static int timeForFullLoop;

	// The start of every players turn will add one to this. When we apply fire/ice we can set the
	// "unfreeze" time to be "currentTime + amount of players". Static so other classes can easily access it.
	private static int currentTime;

	private static boolean loadingSaveFile;
	private static String nextFileToLoad; // Either a save file or level file
	private static String[] nextLevelProfiles; // This will be used if we are loading a completely new game

	@FXML
	private Button saveButton;
	@FXML
	private VBox boardContainer;
	@FXML
	private VBox leftVBox;
	@FXML
	private HBox bottomContainer;

	/**
	 * Represent what "phase" of the game we are in. Allow save files to know what stage of the game they
	 * were at.
	 */
	private enum TurnPhases {
		DRAWING,
		PLACEMENT,
		PLAYACTION,
		MOVEMENT,
		END
	}

	private String currentLevelName; // Name of level we are on, needed to update leaderboards
	private VBox[] playerSubInfoVBoxes;
	private Player[] players;
	private int currentPlayer; // 0 to 3, player that is doing their turn
	private Board board;
	private GridPane renderedBoard;
	private FloorTile floorTileToInsert;
	private TurnPhases currentTurnPhase;
	private ActionTile.ActionType usedAction; // We "used" this action, and are now applying it

	/**
	 * Get the current game time as an int. Will always be above 0.
	 *
	 * @return int representing the game time.
	 */
	public static int getCurrentTime() {
		return currentTime;
	}

	/**
	 * Get the amount of time it takes for all players to complete a turn in this specific game, as this will
	 * change depending on the amount of players.
	 *
	 * @return int showing the time it takes for all players to do a one turn.
	 */
	public static int getTimeForFullLoop() {
		return timeForFullLoop;
	}

	/**
	 * Next time the level scene is loaded, it will build a new game from this level file.
	 *
	 * @param levelName     Level name
	 * @param profilesToUse Array of profile names (the length of which is the amount of players)
	 */
	public static void setNextLevelToLoad(String levelName, String[] profilesToUse) {
		loadingSaveFile = false;
		nextFileToLoad = levelName;
		nextLevelProfiles = profilesToUse;
	}

	/**
	 * Next time the level scene is loaded, it will rebuild the game from the given save name.
	 *
	 * @param saveName Name of save file
	 */
	public static void setNextSaveToLoad(String saveName) {
		loadingSaveFile = true;
		nextFileToLoad = saveName;
		nextLevelProfiles = null; // Not necessary but just to be safe
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LevelController");

		// No matter whether this is a fresh game or a save, the SilkBag must be emptied
		SilkBag.emptyBag();

		if (loadingSaveFile) {
			setupFromSaveFile(nextFileToLoad);
		} else {
			setupFromLevelFile(nextFileToLoad, nextLevelProfiles);
		}
	}

	/**
	 * Go to the level menu
	 *
	 * @param event Click event to get current window.
	 */
	@FXML
	public void goToLevelMenu(ActionEvent event) {
		System.out.println("Going to level menu...");
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Leaving game");
		alert.setHeaderText("You are about to leave this game");
		alert.setContentText("Are you sure to exit? All unsaved progress will be lost!");
		Optional<ButtonType> leave = alert.showAndWait();
		if (leave.get() == ButtonType.OK) {
			try {
				Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_menu.fxml"));
				Scene profileMenuScene = new Scene(profileMenuParent);
				Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

				window.setScene(profileMenuScene);
				window.setTitle("Level Select");
				window.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Increases the size at which tiles render at, maximum of 100.
	 */
	@FXML
	public void increaseZoom() {
		tileRenderSize = Math.min(100, tileRenderSize + 10);
		renderBoard();
		if (currentTurnPhase == TurnPhases.MOVEMENT || (currentTurnPhase == TurnPhases.PLAYACTION && usedAction == ActionTile.ActionType.DOUBLEMOVE)) {
			showWay();
		}
	}

	/**
	 * Decreases the size at which tiles render, minimum of 20.
	 */
	@FXML
	public void decreaseZoom() {
		tileRenderSize = Math.max(20, tileRenderSize - 10);
		renderBoard();
		if (currentTurnPhase == TurnPhases.MOVEMENT || (currentTurnPhase == TurnPhases.PLAYACTION && usedAction == ActionTile.ActionType.DOUBLEMOVE)) {
			showWay();
		}
	}

	/**
	 * exportToSave will collect all necessary information about the game and save it to a file. An alert
	 * will popup to show the save name.
	 *
	 * @throws IOException If it cannot save to file
	 */
	public void exportToSave() throws IOException {

		Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
		saveAlert.setTitle("Save");
		saveAlert.setHeaderText("You are about to save this game");
		saveAlert.setContentText("Are you sure you want to save this game now?");

		Optional<ButtonType> result = saveAlert.showAndWait();
		if (result.get() == ButtonType.OK) {
			String timeStamp = new SimpleDateFormat("ss-mm-HH-dd-MM-yyyy").format(new Date());
			String saveFileName = "save_" + timeStamp + ".ser"; // temp, just take level name
			System.out.println(saveFileName);
			System.out.println("Saving game state to file " + saveFileName);

			// Setup output
			FileOutputStream fos = new FileOutputStream("source/resources/saves/" + saveFileName);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);

			// Serialize the objects from which we could later rebuild the entire game state
			objectOutputStream.writeObject(currentTime);
			objectOutputStream.writeObject(this.currentLevelName);
			objectOutputStream.writeObject(this.players);
			objectOutputStream.writeObject(this.currentPlayer);
			objectOutputStream.writeObject(this.board);
			objectOutputStream.writeObject(this.floorTileToInsert);
			objectOutputStream.writeObject(this.currentTurnPhase);
			objectOutputStream.writeObject(SilkBag.getEntireBag());
			objectOutputStream.flush();
			objectOutputStream.close();

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setContentText("Game saved to save file: " + saveFileName + ". You can load it from the level menu.");
			alert.setTitle("Game Saved");
			alert.setHeaderText(null);
			alert.showAndWait();
		}

	}

	/**
	 * setupNewLevel will build a completely fresh level. Players will be put on their starting locations and
	 * they will have no action tiles. The game will then begin with drawingPhase being called.
	 *
	 * @param levelName   The file name of the level to load from scratch
	 * @param profileInfo String array of profile names to use for this game (they can be null)
	 */
	private void setupFromLevelFile(String levelName, String[] profileInfo) {
		System.out.println("Creating new game from level file...");
		LevelData ld = LevelIO.readDataFile("source/resources/" + levelName + ".txt");

		timeForFullLoop = profileInfo.length;
		currentTime = 0;

		this.currentLevelName = levelName;
		this.currentPlayer = 0;

		//
		// Board Setup
		//
		System.out.println("Setting up board...");

		this.board = ld.getBoard();

		// Add all floor tiles to the silk bag
		for (FloorTile.FloorType floorType : FloorTile.FloorType.values()) {
			int amount = ld.getFloorTileAmount(floorType);
			for (int i = 0; i < amount; i++) {
				SilkBag.addTile(new FloorTile(new Random().nextInt(5), floorType));
			}
		}

		// IMPORTANT: Before we create and add action tiles to the bag, we use the silk bag to fill up
		// the board with random tiles (since we know only floor tiles are stored in the bag right now).
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				if (this.board.getTileAt(x, y) == null) {
					this.board.setTileAt((FloorTile) SilkBag.getRandomTile(), x, y);
				}
			}
		}

		// Create all the action tiles and add them to the Board.
		for (ActionTile.ActionType tileType : ActionTile.ActionType.values()) {
			int amount = ld.getActionTileAmount(tileType);
			for (int i = 0; i < amount; i++) {
				SilkBag.addTile(new ActionTile(tileType));
			}
		}

		//
		// Player Setup
		//
		System.out.println("Setting up players...");

		players = new Player[profileInfo.length];
		for (int i = 0; i < players.length; i++) {
			int associatedProfileID = -1;
			if (nextLevelProfiles[i] != null) {
				// This will not hit a null pointer exception since we JUST came from the level menu,
				// where the profiles were fine.
				associatedProfileID = ProfileManager.getProfileByName(nextLevelProfiles[i]).getID();
			}

			Player newPlayer = new Player(i, associatedProfileID);

			int[] startingPosition = ld.getPlayerStartingPositions()[i];
			newPlayer.setStandingOn(this.board.getTileAt(startingPosition[0], startingPosition[1]));

			newPlayer.addToPastPositions(startingPosition[0], startingPosition[1]);
			newPlayer.addToPastPositions(startingPosition[0], startingPosition[1]);
			newPlayer.addToPastPositions(startingPosition[0], startingPosition[1]);

			players[i] = newPlayer;
		}

		// this.players is now ready, so we can setup the side info with player profile names etc
		setupSideInfo();

		// Once everything is setup, begin the first phase
		drawingPhase();
	}

	/**
	 * setupFromSaveFile will rebuild a previous game from a serialized save file.
	 *
	 * @param saveName The file name of the save file
	 */
	private void setupFromSaveFile(String saveName) {
		FileInputStream fis;
		ObjectInputStream objectInputStream;
		try {
			fis = new FileInputStream("source/resources/saves/" + saveName);
			objectInputStream = new ObjectInputStream(fis);

			currentTime = (int) objectInputStream.readObject();
			this.currentLevelName = (String) objectInputStream.readObject();
			this.players = (Player[]) objectInputStream.readObject();
			this.currentPlayer = (int) objectInputStream.readObject();
			this.board = (Board) objectInputStream.readObject();
			this.floorTileToInsert = (FloorTile) objectInputStream.readObject();
			this.currentTurnPhase = (TurnPhases) objectInputStream.readObject();
			SilkBag.setEntireBag((LinkedList<Tile>) objectInputStream.readObject());

			// To be safe, just re-render everything
			setupSideInfo();
			updateSubInfoVBoxes();
			renderBoard();

			objectInputStream.close();

			// Finally check what phase we loaded and go there to "begin" the game again
			switch (this.currentTurnPhase) {
				case DRAWING:
					drawingPhase();
					break;
				case PLACEMENT:
					placementPhase(floorTileToInsert);
					break;
				case PLAYACTION:
					playActionPhase();
					break;
				case MOVEMENT:
					movementPhase();
					break;
				default:
					System.out.println("Loading from save gave no phase. Game is now soft-locked.");
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error reading save file");
		}
	}

	/**
	 * displays data about players
	 */
	private void setupSideInfo() {
		playerSubInfoVBoxes = new VBox[players.length];

		// Populating leftVBox with player info
		leftVBox.getChildren().clear();
		for (int i = 0; i < this.players.length; i++) {
			leftVBox.getChildren().add(createPlayerInfoVBox(i));
		}
	}

	/**
	 * gets tile from SilkBag and starts placementPhase if tile is FloorTile
	 * and playAction if ActionTile
	 */
	private void drawingPhase() {
		currentTurnPhase = TurnPhases.DRAWING;
		renderBoard();
		updateSubInfoVBoxes();
		bottomContainer.getChildren().clear();

		Button drawButton = new Button("Draw a tile from the silk bag to start your turn");
		drawButton.setOnMouseClicked(event -> {
			Tile received = SilkBag.getRandomTile();
			if (received instanceof FloorTile) {
				placementPhase((FloorTile) received);
			} else {
				ActionTile thisAction = (ActionTile) received;

				// Add 0.5f to the amount the player has (of this action). When we check how many we have in the
				// PlayAction phase, we will round down. At the end of the turn, any actions that have a hanging
				// 0.5f will get rounded up. Because of this we don't have to store instances of ActionTiles.
				players[currentPlayer].setActionAmount(thisAction.getType(), players[currentPlayer].getActionAmount(thisAction.getType()) + 0.5f);
				System.out.println("Player " + currentPlayer + " drew " + thisAction.getType().toString());
				updateSubInfoVBoxes();

				playActionPhase();
			}
		});

		bottomContainer.getChildren().add(drawButton);
	}

	/**
	 * Loads interface for placing and rotating the nextFloorTileToInsert
	 *
	 * @param nextFloorTileToInsert FloorTile to insert
	 */
	private void placementPhase(FloorTile nextFloorTileToInsert) {
		Boolean[][] insertionMask = this.board.getInsertablePositions();
		// With some clever use of the ice actions, we could potentially freeze all columns and rows, therefore
		// the placement phase should check that there is at least one insertable column / row before continuing.
		if (Arrays.asList(insertionMask[0]).contains(true) || Arrays.asList(insertionMask[1]).contains(true)) {
			currentTurnPhase = TurnPhases.PLACEMENT;
			this.floorTileToInsert = nextFloorTileToInsert;
			renderPlacementMenu();
			renderBoard();
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setContentText("You have drawn a floor tile, but unfortunately there are no rows or columns you can currently insert into. Your floor tile will be returned to the silk bag.");
			alert.showAndWait();
			SilkBag.addTile(new FloorTile(nextFloorTileToInsert.getOrientation(),nextFloorTileToInsert.getFloorType()));
			floorTileToInsert = null;
			playActionPhase();
		}
	}

	/**
	 * runs when placement menu initialise and updates
	 */
	private void renderPlacementMenu() {
		bottomContainer.getChildren().clear();

		GridPane rotationControls = new GridPane();
		final int rotationControlSize = 64;

		ImageView clockwise = new ImageView(new Image("source/resources/img/turn_arrow.png", rotationControlSize, rotationControlSize, false, false));
		ImageView aClockwise = new ImageView(new Image("source/resources/img/turn_arrow.png", rotationControlSize, rotationControlSize, false, false));

		clockwise.setScaleX(-1);
		clockwise.setOnMouseClicked(event -> {
			floorTileToInsert.rotateBy(1);
			renderPlacementMenu();
		});
		aClockwise.setOnMouseClicked(event -> {
			floorTileToInsert.rotateBy(-1);
			renderPlacementMenu();
		});

		rotationControls.add(clockwise, 0, 0);
		rotationControls.add(floorTileToInsert.renderTile(rotationControlSize), 1, 0);
		rotationControls.add(aClockwise, 2, 0);

		bottomContainer.getChildren().add(rotationControls);
	}

	/**
	 * inserts floor tile renders board and starts playActionPhase
	 *
	 * @param insertionDirection int from 0 to 3 representing the cardinal directions
	 * @param insertionPoint     int from 0 to max width/height, represents in which row/column to insert into.
	 */
	private void endPlacementPhase(int insertionDirection, int insertionPoint) {
		this.board.insertFloorTile(this.floorTileToInsert, insertionDirection, insertionPoint);
		this.floorTileToInsert = null;
		renderBoard();
		playActionPhase();
	}

	/**
	 * starts playAction
	 */
	private void playActionPhase() {
		currentTurnPhase = TurnPhases.PLAYACTION;
		renderActionMenu();
	}

	/**
	 * During the PlayAction phase, we call renderActionMenu to show what Action Tiles we can use (and how
	 * many we have) in the bottom container.
	 */
	private void renderActionMenu() {
		int actionImageRenderSize = 85;
		bottomContainer.getChildren().clear();
		HBox actionsHBox = new HBox();
		actionsHBox.setAlignment(Pos.TOP_CENTER);
		actionsHBox.setSpacing(15);

		// Render some small UI for every action tile
		for (ActionTile.ActionType at : ActionTile.ActionType.values()) {
			// Actual image of action
			ImageView iv = new ImageView(new Image(at.imageURL, actionImageRenderSize, actionImageRenderSize, false, false));
			StackPane stack = new StackPane(iv);
			stack.setAlignment(Pos.TOP_LEFT);
			iv.setFitHeight(Region.USE_COMPUTED_SIZE);

			// When we re-render we highlight the currently chosen action
			if (at == usedAction) {
				ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", actionImageRenderSize, actionImageRenderSize, false, false));
				chosen.setOpacity(0.5);
				stack.getChildren().addAll(chosen);
			}

			// This will always down cast, so no Math.Floor needed (3.99f -> 4)
			int availableAmount = (int) players[currentPlayer].getActionAmount(at);
			int fullAmount = (int) Math.ceil(players[currentPlayer].getActionAmount(at));

			// If we can actually use this action (we have 1 or more), allow us to click it and use it,
			// otherwise display it "greyed out".
			if (availableAmount >= 1) {
				stack.setOnMouseClicked(event -> {
					this.usedAction = at;
					renderActionMenu();
					renderBoard();
					handleClickATChoice();
				});
			} else {
				Pane cover = new Pane();
				cover.setStyle("-fx-background-color: darkgrey; -fx-opacity: 50%");
				cover.setMaxWidth(actionImageRenderSize);
				cover.setMaxHeight(actionImageRenderSize);
				stack.getChildren().add(cover);
			}

			// Text to show how much of this action we have
			Text numOfTiles = new Text(" " + availableAmount + "/" + fullAmount);
			numOfTiles.setStyle("-fx-font-weight: bold; -fx-font-size: 26px; -fx-stroke: black; -fx-stroke-width: 1px");
			DropShadow shadow = new DropShadow(7, 0, 0, Color.BLACK);
			numOfTiles.setEffect(shadow);
			numOfTiles.setFill(players[currentPlayer].getActionAmount(at) < 1 ? Color.RED : Color.GREEN);
			stack.getChildren().add(numOfTiles);

			actionsHBox.getChildren().add(stack);
		}

		// We don't have to use an Action (even if available), so add a button to just skip to the movement phase
		Button skipButton = new Button("Skip");
		skipButton.setPrefSize(actionImageRenderSize, actionImageRenderSize);
		skipButton.setOnMouseClicked(event -> movementPhase());
		actionsHBox.getChildren().add(skipButton);

		bottomContainer.getChildren().add(actionsHBox);
	}

	/**
	 * prepare movement phase renders board
	 */
	private void movementPhase() {
		currentTurnPhase = TurnPhases.MOVEMENT;
		updateSubInfoVBoxes(); // We could have played an action to get here
		renderBoard();
		usedAction = null;
		bottomContainer.getChildren().clear();
		bottomContainer.getChildren().add(new Text("You must now choose where to move"));
		showWay();
	}

	/**
	 * ends turn changes player
	 */
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
	 * When a player steps onto a Goal, we call this method, which will deal with updating profiles and stopping
	 * the game from progressing.
	 *
	 * @param winningID The player id of the player that won
	 */
	private void playerHasWon(int winningID) {
		Profile winningProfile = players[winningID].getAssociatedProfile();

		// Update the winner's stats
		if (winningProfile != null) {
			winningProfile.addTotalPlayed();
			winningProfile.addWin();
		}

		// Update everyone else
		for (int i = 0; i < players.length; i++) {
			if ((i != winningID) && (players[i].getAssociatedProfile() != null)) {
				players[i].getAssociatedProfile().addTotalPlayed();
				players[i].getAssociatedProfile().addLoss();
			}
		}

		ProfileManager.writeProfilesToFile();

		// Update the leaderboard
		// Get profiles that played
		ArrayList<Integer> profilesThatPlayed = new ArrayList<>();
		for (Player p : players) {
			if (p.getAssociatedProfile() != null) {
				profilesThatPlayed.add(p.getAssociatedProfile().getID());
			}
		}

		// If at least one profile as playing update the leaderboard
		if (profilesThatPlayed.size() > 0) {
			if (winningProfile != null) {
				LevelIO.updateLeaderboard(this.currentLevelName, profilesThatPlayed, winningProfile.getID());
			} else {
				LevelIO.updateLeaderboard(this.currentLevelName, profilesThatPlayed, null);
			}
		}

		String playerName = winningProfile != null ? winningProfile.getName() : "Player " + winningID;
		String winningMessage = playerName + " reached the goal tile first! They are the winner!";
		ImageView playerIcon = new ImageView(new Image("source/resources/img/player_" + winningID + ".png", 50, 50, false, false));
		Button returnButton = new Button("Return to level menu");
		returnButton.setOnAction(this::goToLevelMenu);
		bottomContainer.getChildren().clear();
		bottomContainer.getChildren().addAll(playerIcon, new Text(winningMessage), returnButton);

		// Disable the save button to avoid problems
		saveButton.setDisable(true);
	}

	/**
	 * A dirty hacky method to very slowly find a Player somewhere in a Board. TODO: Replace
	 *
	 * @param playerID playerID
	 * @return index 0 is x coordinate 1 is y
	 */
	private int[] getPlayerXYPosition(int playerID) {
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				FloorTile ft = this.board.getTileAt(x, y);
				if (ft.getPlayer() != null && ft.getPlayer().getIdInGame() == playerID) {
					return new int[]{x, y};
				}
			}
		}
		return null;
	}

	/**
	 * gets positions of all players on board
	 *
	 * @return first index is players ID, second coordinates 0 for x, 1 for y
	 */
	private int[][] getAllPlayersXYPosition() {
		int[][] result = new int[players.length][2];
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				FloorTile ft = this.board.getTileAt(x, y);
				if (ft.getPlayer() != null) {
					result[ft.getPlayer().getIdInGame()][0] = x;
					result[ft.getPlayer().getIdInGame()][1] = y;
				}
			}
		}
		return result;
	}

	/**
	 * handle click to choose available action tile
	 */
	private void handleClickATChoice() {
		System.out.println(usedAction);

		if (usedAction == ActionTile.ActionType.DOUBLEMOVE) {
			showWay();
		}
		if (usedAction == ActionTile.ActionType.BACKTRACK) {
			showPlayersToBacktrack();
		}

	}

	/**
	 * highlights players that can be backtracked
	 */
	private void showPlayersToBacktrack() {
		int[][] positions = getAllPlayersXYPosition();
		for (int i = 0; i < positions.length; i++) {
			int index = canPlayerBeBacktracked(players[i]);
			if (index > 0) {
				setAsBacktrackOption(i, index, positions[i][0], positions[i][1]);
			}
		}
	}

	/**
	 * highlights player to be backtracked
	 *
	 * @param player player ID
	 * @param index  how many turns back
	 * @param x      current position
	 * @param y      current position
	 */
	private void setAsBacktrackOption(int player, int index, int x, int y) {
		ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", tileRenderSize, tileRenderSize, false, false));
		chosen.setOpacity(0.5);
		StackPane optionTile = getStackPaneTileByXY(x, y);
		optionTile.getChildren().add(chosen);

		FloorTile backTile = board.getTileAt(players[player].getPastPositions()[index][0], players[player].getPastPositions()[index][1]);
		optionTile.setOnMouseClicked(event -> {
			players[player].setStandingOn(backTile);
			players[player].setHasBeenBacktracked(true);
			players[currentPlayer].removeAction(usedAction);
			renderBoard();
			movementPhase();
		});
	}

	/**
	 * @param player Player
	 * @return turns to backtrack
	 */
	private int canPlayerBeBacktracked(Player player) {
		int howFar = 0;
		if (!player.getHasBeenBacktracked()) {
			int[][] pos = player.getPastPositions();
			howFar = (board.getTileAt(pos[2][0], pos[2][1]).canMoveTo()) ? 1 : 0;
			howFar = (board.getTileAt(pos[1][0], pos[1][1]).canMoveTo()) ? howFar + 1 : 0;
		}
		return howFar;
	}

	/**
	 * show where can player go
	 */
	private void showWay() {
		int[] pos = getPlayerXYPosition(currentPlayer);
		Boolean[] moveMask = board.getMovableFrom(pos[0], pos[1]);
		boolean isThereAWay = false;
		/*
		 runs setAsWay on neighbours if they exist and player can move there
		 uses index in moveMask to calculate neighbour's coordinate
		*/
		for (int i = 0; i < moveMask.length; i++) {
			if (moveMask[i]) {
				isThereAWay = true;
				if (i % 2 == 0) {
					setAsWay(pos[0], (pos[1] - 1 + i));
				} else {
					setAsWay((pos[0] + 2 - i), pos[1]);
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

	/**
	 * set tile as way to go
	 *
	 * @param x coordinate
	 * @param y coordinate
	 */
	private void setAsWay(int x, int y) {
		ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", tileRenderSize, tileRenderSize, false, false));
		chosen.setOpacity(0.5);
		StackPane wayTile = getStackPaneTileByXY(x, y);
		wayTile.getChildren().add(chosen);
		wayTile.setOnMouseClicked(event -> move(x, y));
	}

	/**
	 * moves player
	 *
	 * @param player Player
	 * @param x      X-position of new tile to move on
	 * @param y      X-position of new tile to move on
	 */
	private void move(Player player, int x, int y) {
		player.setStandingOn(board.getTileAt(x, y));
		player.addToPastPositions(x, y);

		// Check if we moved to a Goal and won. Otherwise continue the phases.
		if (board.getTileAt(x, y).isItGoal()) {
			System.out.println("Player " + currentPlayer + " has won.");
			playerHasWon(currentPlayer);
			currentTurnPhase = TurnPhases.END;
			renderBoard();
		} else {
			// If we moved in the PLAYACTION phase, that means we just double moved and should just continue
			// to the normal movement phase.
			switch (currentTurnPhase) {
				case PLAYACTION:
					// If we were moving in the PLAYACTION phase, we just used a DOUBLEMOVE
					players[currentPlayer].removeAction(usedAction);
					movementPhase();
					break;
				case MOVEMENT:
					endTurn();
					break;
			}
		}
	}

	/**
	 * move currentPlayer
	 *
	 * @param x X-position of new tile to move on
	 * @param y X-position of new tile to move on
	 */
	private void move(int x, int y) {
		move(players[currentPlayer], x, y);
	}

	/**
	 * returns tile as stackPane by coordinates
	 *
	 * @param col column
	 * @param row row
	 * @return StackPane tile
	 */
	private StackPane getStackPaneTileByXY(int col, int row) {
		for (Node node : renderedBoard.getChildren()) {
			if (GridPane.getColumnIndex(node) == col + 1 && GridPane.getRowIndex(node) == row + 1) {
				return (StackPane) node;
			}
		}
		return null;
	}

	/**
	 * handles click events on tiles
	 *
	 * @param x coordinate
	 * @param y coordinate
	 */
	private void handleActionClickOn(int x, int y) {
		switch (usedAction) {
			case FIRE:
				// Fire will only apply and move the turn phase forward if it is able to be applied.
				if (board.canSetOnFire(x, y)) {
					board.setOnFire(x, y);
					players[currentPlayer].removeAction(usedAction);
					movementPhase();
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Cannot apply fire here, there is a player nearby (3 x 3 area)");
					alert.showAndWait();
				}
				break;
			case ICE:
				board.setFreezeOn(x, y);
				players[currentPlayer].removeAction(usedAction);
				movementPhase();
				break;
			case BACKTRACK:
			case DOUBLEMOVE:
				break;
		}
	}

	/**
	 * checks if clicks
	 *
	 * @param x coordinate
	 * @param y coordinate
	 */
	private void handleFloorTileClickAt(int x, int y) {
		if (currentTurnPhase == TurnPhases.PLAYACTION && usedAction != null) {
			handleActionClickOn(x, y);
		}

		System.out.println("This tile's mask is " + Arrays.toString(this.board.getTileAt(x, y).getMoveMask()));
		System.out.println("From this tile you can move to " + Arrays.toString(this.board.getMovableFrom(x, y)));
	}

	/**
	 * renders board
	 */
	private void renderBoard() {
		// Clear the old render
		boardContainer.getChildren().clear();

		renderedBoard = new GridPane();
		renderedBoard.setAlignment(Pos.CENTER);
		boardContainer.setMinHeight((board.getHeight() * tileRenderSize) + (2 * tileRenderSize));
		boardContainer.setMinWidth((board.getWidth() * tileRenderSize) + (2 * tileRenderSize));

		Boolean[][] insertableMask = this.board.getInsertablePositions();
		Image insertionImage = new Image("source/resources/img/insert_arrow.png", tileRenderSize, tileRenderSize, false, false);

		// If we are in the placement phase (i.e. we have a FloorTile), show some additional buttons
		if (floorTileToInsert != null) {
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

				// Uncomment for coordinates
				// stack.getChildren().add(new Text("(" + x + ", " + y + ")"));

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

	/**
	 * updates player info
	 */
	private void updateSubInfoVBoxes() {
		for (int i = 0; i < playerSubInfoVBoxes.length; i++) {
			playerSubInfoVBoxes[i].getChildren().clear();
			String actionAmountText = players[i].getFullActionAmount() == 1 ? " Action Tile" : " Action Tiles";
			playerSubInfoVBoxes[i].getChildren().add(new Text(players[i].getFullActionAmount() + actionAmountText));
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
