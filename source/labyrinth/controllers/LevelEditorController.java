package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import source.labyrinth.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * LevelEditorController is used when editing a game board.
 *
 * @author Fillip Serov
 * @author Morgan Firkins
 * @author Ian Lavin Rady
 * @author Max Lui
 * @author Erik Miller
 * @author Narcis Spanache
 * @author Matthew Fok
 */
public class LevelEditorController implements Initializable {
	private final int maximumPlayerAmount = 4;

	private static String nextFileToLoad; // null if completely new board, name of custom board file otherwise
	private static int tileRenderSize = 64;
	private static float playerToTileScale = 0.6f;

	@FXML
	private VBox boardContainer;
	@FXML
	private HBox bottomContainer;
	@FXML
	private ToggleGroup editingState;

	/**
	 * Represents what editing state we are in right now. From this we can decide what to do on certain events.
	 * i.e. if we are in PLAYERS, clicking should place players, whereas in FIXED_TILES we would place a floortile.
	 */
	private enum EditingState {
		BOARD_SIZE,
		FIXED_TILES,
		SILK_BAG,
		PLAYERS,
		SAVE
	}

	private ArrayList<int[]> playerLocations = new ArrayList<>(); // Player locations, i.e. [[0, 1], [2, 2], ...]
	private Board board;
	private EditingState currentState;
	private FloorTile selectedFloorTile; // A copy of this is placed onto the board
	private ArrayList<FloorTile> fixedTilesControls = new ArrayList<>();

	// Hashmap for storing silk bag info. The String key is an enum value from FloorTile.FloorType or ActionTile.ActionType
	private HashMap<String, Integer> silkbagAmounts;

	/**
	 * Used to set the next file that will be loaded to the editor (when the scene is created).
	 *
	 * @param newFile Level file name in resources/custom_levels/, null if creating a new board
	 */
	public static void setNextFileToLoad(String newFile) {
		nextFileToLoad = newFile;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LevelEditorController");

		for (FloorTile.FloorType type : FloorTile.FloorType.values()) {
			fixedTilesControls.add(new FloorTile(2, type, true));
		}

		LevelData ld = null;
		if (nextFileToLoad != null) {
			ld = LevelIO.readDataFile("source/resources/custom_levels/" + nextFileToLoad + ".txt");
			board = ld.getBoard();
		} else {
			board = new Board(0, 0);
		}

		if (ld != null) {
			int[][] previousPlayers = ld.getPlayerStartingPositions();
			for (int[] playerLocation : previousPlayers) {
				playerLocations.add(new int[]{playerLocation[0], playerLocation[1]});
			}
		}

		// This is slightly messy, but basically:
		// Fill up silkbagAmounts hashmap with needed values (ICE, FIRE, TSHAPE, GOAL, etc)
		// And set the amount to amount loaded from the level file, 0 otherwise
		silkbagAmounts = new HashMap<>();
		for (ActionTile.ActionType actionType : ActionTile.ActionType.values()) {
			silkbagAmounts.put(actionType.name(), (ld != null ? ld.getActionTileAmount(actionType) : 0));
		}
		for (FloorTile.FloorType floorType : FloorTile.FloorType.values()) {
			silkbagAmounts.put(floorType.name(), (ld != null ? ld.getFloorTileAmount(floorType) : 0));
		}

		editingState.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
			RadioButton modeSelected = (RadioButton) editingState.getSelectedToggle();
			currentState = EditingState.valueOf(modeSelected.getId());
			System.out.println("Changed currentState to: " + currentState);

			updateBottomContainer();
			renderBoard();//change ToolTip
		}));

		currentState = EditingState.BOARD_SIZE;
		updateBottomContainer();
		renderBoard();
	}

	/**
	 * Validates that level is correct before writing to file
	 * Checks:
	 * - Enough tiles in silkbag for the board
	 * - There is at least one goal tile
	 * - That four player positions have been selected
	 *
	 * @return Alert - Returns an alert if conditions not me; if null then program continues
	 */
	private Alert validateLevel() {
		String errorTitle = "The following errors have occured in your board settings:\n";
		String errorLog = "";

		int boardAreaToFill = (board.getHeight() * board.getWidth()) - getCurrentFixedTileAmount();
		int silkBagAccumulator = silkbagAmounts.get("STRAIGHT") + silkbagAmounts.get("TSHAPE") +
				silkbagAmounts.get("CORNER") + silkbagAmounts.get("GOAL");

		boolean hasAtLeastOneGoal = false;
		for (int x = 0; x < board.getWidth(); x++) {
			// TODO: Inefficient, loops through everything even if goal already found
			for (int y = 0; y < board.getHeight(); y++) {
				if (board.getTileAt(x, y) != null && board.getTileAt(x, y).getFloorType() == FloorTile.FloorType.GOAL) {
					hasAtLeastOneGoal = true;
				}
			}
		}

		if (silkBagAccumulator < boardAreaToFill) {
			errorLog += "-> There are not enough floor tiles in the silk bag, " +
					"please increase this to at least the area of the board\n";
		}

		if (!hasAtLeastOneGoal) {
			errorLog += "-> There has to be AT LEAST ONE goal tile on the board, please add a goal tile\n";
		}

		if (playerLocations.size() < maximumPlayerAmount) {
			errorLog += "-> All player positions have to be defined, please define them\n";
		}

		boolean playerStartsOnGoal = playerLocations.stream().anyMatch(location -> {
			// Prevent null pointer exceptions doing it this way
			FloorTile maybeGoal = board.getTileAt(location[0], location[1]);
			return maybeGoal != null && maybeGoal.getFloorType() == FloorTile.FloorType.GOAL;
		});
		if (playerStartsOnGoal) {
			errorLog += "-> One of your players will start on a goal tile, please move them elsewhere\n";
		}

		if (errorLog.isEmpty()) {
			return null;
		} else {
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("An Error has Occured");
			errorAlert.setHeaderText("There is a problem with the board settings");
			String errorContent = errorTitle + errorLog;
			errorAlert.setContentText(errorContent);
			return errorAlert;
		}


	}

	/**
	 * Checks whether the given string is a valid filename
	 *
	 * @param fileName Filename to check
	 * @return An instance of Alert if an error was found, null otherwise
	 */
	private Alert validateFileName(String fileName) {
		String errorTitle = "The following errors have occured in your file name:\n";
		String errorLog = "";
		if (fileName.contains(".")) {
			errorLog += "-> File Name contains full stops(.)\n";
		}
		if (fileName.contains(":")) {
			errorLog += "-> File Name contains colon(:)\n";

		}
		if (fileName.contains("/") || fileName.contains("\\")) {
			errorLog += "-> File Name contains a slash (not the guitarist)(/ or \\)\n";
		}

		if (fileName.contains("#")) {
			errorLog += "-> File Name contains a pound/hashtag(#)\n";
		}

		if (fileName.contains("%")) {
			errorLog += "-> File Name contains a percent(%)\n";
		}
		if (fileName.contains("&")) {
			errorLog += "-> File Name contains an ampersand(&)\n";
		}
		if (fileName.contains("{") || fileName.contains("}")) {
			errorLog += "-> File Name contains a curly bracket({ or })\n";
		}
		if (fileName.contains("<") || fileName.contains(">")) {
			errorLog += "-> File Name contains an angled bracket(< or >)\n";
		}
		if (fileName.contains("*")) {
			errorLog += "-> File Name contains an asterisk(*)\n";
		}
		if (fileName.contains("?")) {
			errorLog += "-> File Name contains a question mark(?)\n";
		}
		if (fileName.contains("$")) {
			errorLog += "-> File Name contains a dollar sign($)\n";
		}
		if (fileName.contains("!")) {
			errorLog += "-> File Name contains an exclamation mark(!)\n";
		}
		if (fileName.contains("'") || fileName.contains("\"")) {
			errorLog += "-> File Name contains a quotation marks(' or \")\n";
		}
		if (fileName.contains("@")) {
			errorLog += "-> File Name contains an at symbol(@)\n";
		}
		if (fileName.contains("+")) {
			errorLog += "-> File Name contains an addition symbol(+)\n";
		}
		if (fileName.contains("`")) {
			errorLog += "-> File Name contains a back tick(`)\n";
		}
		if (fileName.contains("|")) {
			errorLog += "-> File Name contains a pipe(|)\n";
		}
		if (fileName.contains("`")) {
			errorLog += "-> File Name contains an equals symbol(=)\n";
		}
		if (fileName == null || fileName.isEmpty() || fileName.contains(" ")) {
			errorLog += "-> File Name is blank";
		}
		if (errorLog == null || errorLog.isEmpty() || errorLog.contains(" ")) {
			return null;
		} else {
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("An Error has Occured");
			errorAlert.setHeaderText("There is a problem with your file name");
			String errorContent = errorTitle + errorLog;
			errorAlert.setContentText(errorContent);
			return errorAlert;
		}

	}

	public void textDialogOk(TextInputDialog textDialog) throws IOException {
		String fileName = textDialog.getEditor().getText();
		System.out.println("User gave filename: " + fileName);
		Alert errorDialog = validateFileName(fileName);
		Alert confirmDialog = new Alert(Alert.AlertType.INFORMATION, "Level saved", ButtonType.OK);
		confirmDialog.setHeaderText("Level " + fileName + " saved successfully");
		if (errorDialog != null) {
			errorDialog.showAndWait();
			saveChanges();//show up the textDialog again for user to re-enter the name
		} else {
			fileName += ".txt";
			File filePath = new File("source\\resources\\custom_levels" + "\\" + fileName);
			System.out.println(filePath);
			if (filePath.exists()) {
				String dialogMsg = fileName + " already exists. Are you sure you want to overwrite it?";
				Alert overwriteDialog = new Alert(Alert.AlertType.CONFIRMATION, dialogMsg, ButtonType.YES, ButtonType.CANCEL);
				overwriteDialog.showAndWait();

				if (overwriteDialog.getResult() == ButtonType.YES) {
					Alert validateDialog = validateLevel();
					if (validateDialog != null) {
						validateDialog.showAndWait();
					} else {
						fileWriter(filePath);
						confirmDialog.showAndWait();
					}
				} else {
					overwriteDialog.close();
				}
			} else {
				Alert validateDialog = validateLevel();
				if (validateDialog != null) {
					validateDialog.showAndWait();
				} else {
					fileWriter(filePath);
					confirmDialog.showAndWait();
				}
			}
		}
	}

	/**
	 * Begins the process to save a custom level to file, dealing with level validation, filename validation,
	 * checking for existing files along the way
	 *
	 * @throws IOException If a file error occured
	 */
	@FXML
	public void saveChanges() throws IOException {//removed unused Action event parameter, unexpected problem may happen
		// If nextFileToLoad is null, it will just be empty, otherwise shows the name of the level we loaded
		TextInputDialog textDialog = new TextInputDialog(nextFileToLoad);

		textDialog.setTitle("Save your Level");
		textDialog.setHeaderText("Give Your Level A Name");

		Boolean isEmpty = false;
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				FloorTile current = this.board.getTileAt(x, y);
				if (current == null) {
					isEmpty = true;
					break;
				}
			}
		}
		if (isEmpty) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Random Tiles For Empty Tiles");
			alert.setHeaderText("Notice");
			alert.setContentText("Empty tiles will be filled randomly with tiles from silk bag, ready to save?");
			Optional<ButtonType> random = alert.showAndWait();
			if (random.get() == ButtonType.OK) {
				Optional<String> result = textDialog.showAndWait();
				if (result.isPresent()) {
					textDialogOk(textDialog);

				}
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Non-Playable Board");
			alert.setHeaderText("No empty tiles for silk bag");
			alert.setContentText("You have to put some empty tiles on board so as to draw and put tiles during a game.");
			alert.showAndWait();
		}


	}

	/**
	 * Go back to the editor menu
	 *
	 * @param event Event to get scene from
	 */
	@FXML
	public void returnToEditorMenu(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Leaving");
		alert.setHeaderText("You are about to leave");
		alert.setContentText("Are you sure want to leave? All unsaved progress will be discard! Don't blame me if your board are not save yet ^o^");
		Optional<ButtonType> leave = alert.showAndWait();
		if (leave.get() == ButtonType.OK) {

			System.out.println("Editor Menu");
			try {
				Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor_menu.fxml"));
				Scene profileMenuScene = new Scene(profileMenuParent);
				Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

				window.setScene(profileMenuScene);
				window.setTitle("Editor Menu");
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
	}

	/**
	 * Decreases the size at which tiles render, minimum of 20.
	 */
	@FXML
	public void decreaseZoom() {
		tileRenderSize = Math.max(20, tileRenderSize - 10);
		renderBoard();
	}

	/**
	 * Checks the active editing tab to show the relevant controls
	 */
	private void updateBottomContainer() {
		bottomContainer.getChildren().clear();

		switch (currentState) {
			case BOARD_SIZE:
				selectBoardSize();
				break;
			case FIXED_TILES:
				showFixedTileControls();
				break;
			case SILK_BAG:
				showSilkbagControls();
				break;
			case PLAYERS:
				bottomContainer.getChildren().add(new Text("Left click to place a player\n" +
						"Right click to remove a player\n" +
						"There can be at most " + maximumPlayerAmount + " player positions"));
				break;
			case SAVE:
				bottomContainer.getChildren().add(new Text("Save stuff"));
				break;
		}
	}

	/**
	 * Select the width and the height of the board that you want to create
	 * Generates a board with 4 fixed corner tiles and a goal tile
	 * You can always go to the default board by pressing the default button
	 */
	private void selectBoardSize() {
		TextField width = new TextField();
		width.setPromptText("Enter width.");

		Button widthIncreaseButton = new Button("+");
		widthIncreaseButton.setOnAction(event -> {
			board.changeSize(board.getWidth() + 1, board.getHeight());
			validatePlayerLocations();
			renderBoard();
		});

		Button widthDecreaseButton = new Button("-");
		widthDecreaseButton.setOnAction(event -> {
			board.changeSize(board.getWidth() - 1, board.getHeight());
			validatePlayerLocations();
			renderBoard();
		});

		final Tooltip widthIncTip = new Tooltip("increase board width");
		final Tooltip widthDecTip = new Tooltip("decrease board width");
		widthIncTip.setStyle("-fx-font-size: 16");
		widthDecTip.setStyle("-fx-font-size: 16");
		showToolTip(widthIncreaseButton, widthIncTip);
		showToolTip(widthDecreaseButton, widthDecTip);

		bottomContainer.getChildren().addAll(width, widthIncreaseButton, widthDecreaseButton);

		TextField height = new TextField();
		height.setPromptText("Enter height.");

		Button heightIncreaseButton = new Button("+");
		heightIncreaseButton.setOnAction(event -> {
			board.changeSize(board.getWidth(), board.getHeight() + 1);
			validatePlayerLocations();
			renderBoard();
		});

		Button heightDecreaseButton = new Button("-");
		heightDecreaseButton.setOnAction(event -> {
			board.changeSize(board.getWidth(), board.getHeight() - 1);
			validatePlayerLocations();
			renderBoard();
		});

		final Tooltip heightIncTip = new Tooltip("increase board height");
		final Tooltip heightDecTip = new Tooltip("decrease board height");
		heightIncTip.setStyle("-fx-font-size: 16");
		heightDecTip.setStyle("-fx-font-size: 16");
		showToolTip(heightIncreaseButton, heightIncTip);
		showToolTip(heightDecreaseButton, heightDecTip);

		bottomContainer.getChildren().addAll(height, heightIncreaseButton, heightDecreaseButton);

		Button submit = new Button("Set board");
		bottomContainer.getChildren().add(submit);
		submit.setOnAction((event) -> {
			int newWidth;
			int newHeight;
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Error");
			try {//stop users from entering things other than required
				if (width.getText().equals("") || height.getText().equals("")) {
					alert.setHeaderText("Why is it empty?");
					alert.setContentText("You have to enter integer for both width and height before submitting the size.");
					alert.showAndWait();
					return;
				} else if (Integer.parseInt(width.getText()) < 0 || Integer.parseInt(width.getText()) < 0) {
					alert.setHeaderText("Are you trying to create a 4-Dimension board?");
					alert.setContentText("You have to enter a positive integer, negative value is not allowed.");
					alert.showAndWait();
					width.clear();
					height.clear();
					return;
				} else if (Integer.parseInt(width.getText()) == 0 || Integer.parseInt(width.getText()) == 0) {
					alert.setHeaderText("you created a 0-Dimension board ?");
					alert.setContentText("You have to enter a positive integer, but not trying to create a void world");
					alert.showAndWait();
					width.clear();
					height.clear();
					return;
				}
				newWidth = Integer.parseInt(width.getText());
				newHeight = Integer.parseInt(height.getText());
			} catch (NumberFormatException e) {
				alert.setHeaderText("Are you trying to break me?");
				alert.setContentText("You have to enter INTEGER, not other things!");
				alert.showAndWait();
				width.clear();
				height.clear();
				return;
			}


			board = new Board(newWidth, newHeight);

			FloorTile fixedTile = new FloorTile(1, FloorTile.FloorType.CORNER);
			fixedTile.setFixed(true);
			board.setTileAt(fixedTile, 0, 0);

			FloorTile fixedTile1 = new FloorTile(2, FloorTile.FloorType.CORNER);
			fixedTile1.setFixed(true);
			board.setTileAt(fixedTile1, newWidth - 1, 0);

			FloorTile fixedTile2 = new FloorTile(3, FloorTile.FloorType.CORNER);
			fixedTile2.setFixed(true);
			board.setTileAt(fixedTile2, newWidth - 1, newHeight - 1);

			FloorTile fixedTile3 = new FloorTile(0, FloorTile.FloorType.CORNER);
			fixedTile3.setFixed(true);
			board.setTileAt(fixedTile3, 0, newHeight - 1);

			if (newWidth > 2 && newHeight > 2) {
				FloorTile fixedTile4 = new FloorTile(0, FloorTile.FloorType.GOAL);
				fixedTile4.setFixed(true);
				board.setTileAt(fixedTile4, newWidth / 2, newHeight / 2);
			}

			validatePlayerLocations();
			renderBoard();
		});
		final Tooltip submitTip = new Tooltip("Set the width and height of the board\nRequired a positive integer for both value");
		submitTip.setStyle("-fx-font-size: 16");
		showToolTip(submit, submitTip);

		Button clear = new Button("Clear");
		clear.setOnAction((event) -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Resetting current board");
			alert.setHeaderText("Nuclear Launch Confirmation");
			alert.setContentText("Are you sure to destroy this board? It is irreversible");
			Optional<ButtonType> destroy = alert.showAndWait();
			if (destroy.get() == ButtonType.OK) {
				width.clear();
				height.clear();
				board = new Board(0, 0);
				boardContainer.getChildren().clear();
				renderBoard();
			}
		});

		//helper text
		final Tooltip clearTip = new Tooltip("destroy the whole board that you created above");
		clearTip.setStyle("-fx-font-size: 16");
		showToolTip(clear, clearTip);

		bottomContainer.getChildren().add(clear);

		/*Button defaultt = new Button("Default");
		defaultt.setOnAction((event) -> {
			String levelName = "example_level";

			LevelData ld = LevelIO.readDataFile("source/resources/levels/" + levelName + ".txt");
			board = ld.getBoard();
			renderBoard();
		});
		bottomContainer.getChildren().add(defaultt);*/
	}

	/**
	 * Checks all existing players to make sure they fall under the board boundaries. If they don't, get rid of them.
	 */
	private void validatePlayerLocations() {
		playerLocations.removeIf(location -> {
			return location[0] > (board.getWidth() - 1) || location[1] > (board.getHeight() - 1);
		});
	}

	/**
	 * Get the amount of placed fixed tiles on the board right now
	 *
	 * @return Amonut of fixed tiles on the board
	 */
	private int getCurrentFixedTileAmount() {
		int amountOfFixedTiles = 0;

		for (int x = 0; x < board.getWidth(); x++) {
			for (int y = 0; y < board.getHeight(); y++) {
				if (board.getTileAt(x, y) != null) {
					amountOfFixedTiles++;
				}
			}
		}

		return amountOfFixedTiles;
	}

	/**
	 * Rotates the currently selected floor tile (if it's a real tile)
	 *
	 * @param rot Amount to rotate by, can be negative
	 */
	private void rotateSelectedFloorTile(int rot) {
		if ((currentState == EditingState.FIXED_TILES) && (selectedFloorTile != null)) {
			selectedFloorTile.rotateBy(rot);
			updateBottomContainer();
		}
	}

	/**
	 * Shows the 4 floor tiles (and a fifth empty one) that you can click and place. Assumed that
	 * bottomContainer is cleared out already before this method is called.
	 */
	private void showFixedTileControls() {
		int fixedTileImageSize = 64;

		// We'll place this overlay on whatever we have selected, which we will figure out below
		ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", fixedTileImageSize, fixedTileImageSize, false, false));
		chosen.setOpacity(0.5);

		// Create image buttons for the four types of floor tile
		for (FloorTile tile : fixedTilesControls) {
			StackPane stackTile = tile.renderTile(fixedTileImageSize);

			String tileTypeHelp = "";
			switch (tile.getFloorType()) {
				case STRAIGHT:
					tileTypeHelp = "Straight Tile:";
					break;
				case CORNER:
					tileTypeHelp = "Corner Tile:";
					break;
				case TSHAPE:
					tileTypeHelp = "T Tile:";
					break;
				case GOAL:
					tileTypeHelp = "Goal Tile:";
					break;
			}

			// Tool tip text
			final Tooltip tileTip = new Tooltip(tileTypeHelp + "\nLeft click to select the tile" +
					"\nClick an empty tile on board to place it" +
					"\nClick an exist tile on board to replace with it." +
					"\nPress 'A' to rotate tile to left\nPress 'D' to rotate tile to right");
			tileTip.setStyle("-fx-font-size: 16");
			showToolTip(stackTile, tileTip);

			stackTile.setOnMouseClicked(event -> {
				// setSelectedFloorTile(new FloorTile(tile.getOrientation(), tile.getFloorType(), true));
				setSelectedFloorTile(tile);
				updateBottomContainer();
			});

			if ((selectedFloorTile != null) && (tile.getFloorType() == selectedFloorTile.getFloorType())) {
				stackTile.getChildren().add(chosen);
			}

			Scene scene = boardContainer.getScene();
			scene.setOnKeyPressed((key) -> {
				if (key.getCode() == KeyCode.A) {
					rotateSelectedFloorTile(-1);
				}
				if (key.getCode() == KeyCode.D) {
					rotateSelectedFloorTile(1);
				}
			});

			bottomContainer.getChildren().add(stackTile);
		}

		// Add a fifth option, which will place an "empty" tile (basically deletion)
		Image img = new Image("source/resources/img/tile_none.png", fixedTileImageSize, fixedTileImageSize, false, false);
		StackPane stack = new StackPane(new ImageView(img));
		stack.setOnMouseClicked(event -> {
			setSelectedFloorTile(null);
			updateBottomContainer();
		});

		if (selectedFloorTile == null) {
			stack.getChildren().add(chosen);
		}

		// Tool tip text
		final Tooltip clearTip = new Tooltip("Empty Tile:\n" + "Clear the selected tile on board\n" + "Left click to select the tile");
		clearTip.setStyle("-fx-font-size: 16");
		showToolTip(stack, clearTip);

		bottomContainer.getChildren().add(stack);
	}

	/**
	 * Show all eight tiles with an amount that the user can change. Assumed bottomContainer is cleared out before.
	 */
	private void showSilkbagControls() {
		String[] displayOrder = {"STRAIGHT", "TSHAPE", "CORNER", "GOAL", "ICE", "FIRE", "DOUBLEMOVE", "BACKTRACK"};

		for (String tileTypeName : displayOrder) {
			VBox generalVbox = new VBox();
			HBox imgAndControls = new HBox();

			generalVbox.getChildren().add(imgAndControls);
			generalVbox.setAlignment(Pos.CENTER);

			String imageURL = "source/resources/img/tile_none.png";

			// TODO: Do something better than just 2 dumb loops
			for (FloorTile.FloorType ft : FloorTile.FloorType.values()) {
				if (ft.name().equals(tileTypeName)) {
					imageURL = ft.imageURL;
					break;
				}
			}
			for (ActionTile.ActionType at : ActionTile.ActionType.values()) {
				if (at.name().equals(tileTypeName)) {
					imageURL = at.imageURL;
					break;
				}
			}

			// Tile image
			ImageView tileImg = new ImageView(new Image(imageURL, tileRenderSize, tileRenderSize, false, false));
			imgAndControls.getChildren().add(tileImg);

			//helper text
			String tileHelp1 = "";//tile name
			String tileHelp2 = "";//descriptions of that tile
			if (tileTypeName.equals("STRAIGHT")) {
				tileHelp1 = "Straight Tile";
				tileHelp2 = "";
			}
			if (tileTypeName.equals("TSHAPE")) {
				tileHelp1 = "T Tile";
				tileHelp2 = "";
			}
			if (tileTypeName.equals("CORNER")) {
				tileHelp1 = "Corner Tile";
				tileHelp2 = "";
			}
			if (tileTypeName.equals("GOAL")) {
				tileHelp1 = "Goal Tile";
				tileHelp2 = "\nPlayer win when reach this tiles";
			}
			if (tileTypeName.equals("ICE")) {
				tileHelp1 = "Ice Tile";
				tileHelp2 = "\nFreeze a 3 x 3 area with a selected tiles as centre\nThe rows and columns that being affected will not able to move" +
						"\nPlayers are still able to travel on these frozen tiles\nThe frozen tiles melts away at the start of your next turn.";
			}
			if (tileTypeName.equals("FIRE")) {
				tileHelp1 = "Fire Tile";
				tileHelp2 = "\nBurn a 3 x 3 area with a selected tiles as centre\nPlayers are not able to end turn on a burning tile.\nFire will be extinguished after your turn end.";
			}
			if (tileTypeName.equals("DOUBLEMOVE")) {
				tileHelp1 = "Double Move Tile";
				tileHelp2 = "\nApply to move self twice";
			}
			if (tileTypeName.equals("BACKTRACK")) {
				tileHelp1 = "Back Track Tile";
				tileHelp2 = "\nMove a rival to the tile where they end up before 2 turns,\nwhich cannot be on fire currently.\nThe current tiles remain unchanged.";
			}
			final Tooltip TileTip = new Tooltip(tileHelp1 + tileHelp2);
			TileTip.setStyle("-fx-font-size: 16");
			showToolTip(tileImg, TileTip);

			// Arrow/Button controls
			VBox arrowButtonBox = new VBox();
			arrowButtonBox.setMinWidth(40);
			arrowButtonBox.setAlignment(Pos.CENTER);

			// Number field, needs to be initialized earlier than arrow buttons
			TextField numField = new TextField(silkbagAmounts.get(tileTypeName).toString());
			numField.setMaxWidth(50);
			numField.setOnKeyReleased(event -> {
				if (event.getCode() == KeyCode.ENTER) {
					try {
						int newValue = Integer.parseInt(numField.getText());
						newValue = newValue > -1 ? newValue : 0; // If user put negative number, make it 0

						silkbagAmounts.put(tileTypeName, newValue);
						numField.setText(String.valueOf(newValue)); // User could have put "025" or something similar
						System.out.println(tileTypeName+"tiles number set");
					} catch (NumberFormatException e) {
						numField.setText(silkbagAmounts.get(tileTypeName).toString());
					}
				}
			});
			//easier for user to change value
			numField.setOnMouseClicked(event2 ->numField.selectAll());
			//helper text
			final Tooltip numTile = new Tooltip("Input an integer to set the number of " + tileHelp1 + " in silk bag.\nPress 'Enter' to set the number for that tiles");
			numTile.setStyle("-fx-font-size: 16");
			showToolTip(numField, numTile);

			// Increase button
			Button increase = new Button("▲");
			increase.setOnAction(event -> {
				int newValue = silkbagAmounts.get(tileTypeName) + 1;
				silkbagAmounts.put(tileTypeName, newValue);
				numField.setText(String.valueOf(newValue));
			});
			//helper text
			final Tooltip incBtTip = new Tooltip("Increase number of " + tileHelp1 + " in Silk Bag");
			incBtTip.setStyle("-fx-font-size: 16");
			showToolTip(increase, incBtTip);

			// Decrease button
			Button decrease = new Button("▼");
			decrease.setOnAction(event -> {
				int newValue = silkbagAmounts.get(tileTypeName) - 1;
				if (newValue > -1) {
					silkbagAmounts.put(tileTypeName, newValue);
					numField.setText(String.valueOf(newValue));
				}
			});
			//helper text
			final Tooltip decBtTip = new Tooltip("Decrease number of " + tileHelp1 + " in Silk Bag");
			decBtTip.setStyle("-fx-font-size: 16");
			showToolTip(decrease, decBtTip);

			arrowButtonBox.getChildren().addAll(increase, decrease);
			imgAndControls.getChildren().add(arrowButtonBox);

			generalVbox.getChildren().add(numField);

			bottomContainer.getChildren().add(generalVbox);
		}
	}

	/**
	 * Set the currently selected floor tile. If null then it will "delete" tiles
	 *
	 * @param tile instance of FloorTile to duplicate, null for placing "empty" tiles
	 */
	private void setSelectedFloorTile(FloorTile tile) {
		selectedFloorTile = tile;
	}

	/**
	 * Places the player at the selected tile (x, y). If there are already four defined positions, removes the
	 * oldest one.
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void placePlayerAt(int x, int y) {
		boolean playerAlreadyExistsHere = playerLocations.stream().anyMatch(location -> {
			return location[0] == x && location[1] == y;
		});

		if (!playerAlreadyExistsHere) {
			if (playerLocations.size() >= maximumPlayerAmount) {
				playerLocations.remove(0);
			}

			playerLocations.add(new int[]{x, y});
			System.out.println("Placed player at " + x + ", " + y);
		}
	}

	/**
	 * Remove the player at the selected tile(x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void removePlayerAt(int x, int y) {
		playerLocations.removeIf(location -> location[0] == x && location[1] == y);
	}

	/**
	 * Places the currently selected tile at (x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void placeFixedTileAt(int x, int y) {
		// Deep copy the floor tile, otherwise rotating one will rotate them all
		if (selectedFloorTile != null) {
			FloorTile copy = new FloorTile(selectedFloorTile.getOrientation(), selectedFloorTile.getFloorType(), selectedFloorTile.getFixed());
			board.setTileAt(copy, x, y);
		} else {
			deleteFixedTileAt(x, y);
		}
	}

	/**
	 * Deletes the selected tile at (x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void deleteFixedTileAt(int x, int y) {
		board.setTileAt(null, x, y);
	}

	/**
	 * Rotates the fixed tile at (x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void rotateFixedTileAt(int x, int y) {
		if (board.getTileAt(x, y) != null) board.getTileAt(x, y).rotateBy(1);
	}

	/**
	 * Handle when a floor tile is clicked. Depending on what editing state we are in, different things
	 * will occur (placing a floor tile, placing a player position etc).
	 *
	 * @param e MouseEvent that occurred, used for determining left/right/middle clicks
	 * @param x X-coord clicked on
	 * @param y Y-coord clicked on
	 */
	private void handleFloorTileClickAt(MouseEvent e, int x, int y) {
		switch (currentState) {
			case FIXED_TILES:
				switch (e.getButton()) {
					case PRIMARY:
						placeFixedTileAt(x, y);
						break;
					case MIDDLE:
						rotateFixedTileAt(x, y);
						break;
					case SECONDARY:
						deleteFixedTileAt(x, y);
						break;
				}
				break;
			case PLAYERS:
				switch (e.getButton()) {
					case PRIMARY:
						placePlayerAt(x, y);
						break;
					case SECONDARY:
						removePlayerAt(x, y);
						break;
				}
				break;
		}

		renderBoard();
	}

	/**
	 * Displays the board as a GridPane. Similar to LevelControllers renderBoard.
	 */
	private void renderBoard() {
		GridPane renderedBoard = new GridPane();
		renderedBoard.setAlignment(Pos.CENTER);
		boardContainer.setMinHeight((board.getHeight() * tileRenderSize) + (2 * tileRenderSize));
		boardContainer.setMinWidth((board.getWidth() * tileRenderSize) + (2 * tileRenderSize));

		// The actual board render
		for (int x = 0; x < this.board.getWidth(); x++) {
			for (int y = 0; y < this.board.getHeight(); y++) {
				FloorTile current = this.board.getTileAt(x, y);
				StackPane stack;


				if (current != null) {
					stack = current.renderTile(tileRenderSize);
					//helper text
					if (currentState.equals(EditingState.FIXED_TILES)) {
						final Tooltip TileTip = new Tooltip("Left click - replace tile\n" + "Right click - delete tile\n" + "Middle click - rotate");
						TileTip.setStyle("-fx-font-size: 16");
						showToolTip(stack, TileTip);
					}


				} else {
					Image img = new Image("source/resources/img/tile_none.png", tileRenderSize, tileRenderSize, false, false);
					ImageView iv = new ImageView(img);
					stack = new StackPane(iv);
					//helper text
					if (currentState.equals(EditingState.FIXED_TILES)) {
						final Tooltip emptyTip = new Tooltip("Left click - place tile");
						emptyTip.setStyle("-fx-font-size: 16");
						showToolTip(stack, emptyTip);
					}
				}
				if (currentState.equals(EditingState.PLAYERS)) {
					final Tooltip playerTip = new Tooltip("Left click - place player");
					playerTip.setStyle("-fx-font-size: 16");
					showToolTip(stack, playerTip);
				}

				// Uncomment for coordinates
				// stack.getChildren().add(new Text("(" + x + ", " + y + ")"));

				int finalX = x;
				int finalY = y;
				stack.setOnMouseClicked(event -> {
					handleFloorTileClickAt(event, finalX, finalY);
				});

				renderedBoard.add(stack, x, y);
			}
		}

		// Now render the player positions, after the entire gridpane is populated
		playerLocations.forEach(location -> {
			// There is no way to get a gridpane node via coordinates, so have to use a little hacky calculation
			int stackpaneLocation = location[0] * board.getHeight() + location[1];
			StackPane relevantStackPane = (StackPane) renderedBoard.getChildren().get(stackpaneLocation);
			if (currentState.equals(EditingState.PLAYERS)) {
				final Tooltip player2Tip = new Tooltip("Right click - delete player");
				player2Tip.setStyle("-fx-font-size: 16");
				showToolTip(relevantStackPane, player2Tip);
			}

			ImageView playerImage = new ImageView(new Image("source/resources/img/player_default.png", tileRenderSize * playerToTileScale, tileRenderSize * playerToTileScale, false, false));
			relevantStackPane.getChildren().add(playerImage);

		});

		boardContainer.getChildren().clear();
		boardContainer.getChildren().add(renderedBoard);
	}

	/**
	 * Takes the current board that the user has been editing and saves it to file.
	 *
	 * @param filename Filename to save to
	 * @throws IOException If a file error occurs
	 */
	@FXML
	private void fileWriter(File filename) throws IOException {
		FileWriter writer = new FileWriter(filename);
		writer.write(board.getWidth() + "," + board.getHeight() + "\n");
		writer.write(getCurrentFixedTileAmount() + "\n");

		for (int x = 0; x < board.getWidth(); x++) {
			for (int y = 0; y < board.getHeight(); y++) {
				if (board.getTileAt(x, y) != null) {
					FloorTile fixedTile = board.getTileAt(x, y);
					writer.write(x + "," + y + "," + fixedTile.getFloorType() + "," + fixedTile.getOrientation() + "\n");
				}
			}
		}

		for (int[] location : playerLocations) {
			writer.write(location[0] + "," + location[1] + "\n");
		}

		String[] writeOrder = {"STRAIGHT", "TSHAPE", "CORNER", "GOAL", "ICE", "FIRE", "DOUBLEMOVE", "BACKTRACK"};
		for (String tileType : writeOrder) {
			writer.write(silkbagAmounts.get(tileType) + "," + tileType + "\n");
		}
		writer.close();
	}


	/**
	 * replace the original install method from tooltips to remove delay time
	 *
	 * @param tile Node object
	 * @param tooltip Tooltip object
	 */
	private void showToolTip(final Node tile, final Tooltip tooltip) {
		tile.setOnMouseMoved(event -> {
			//set the tooltip location at cursor below
			tooltip.show(tile, event.getScreenX() + 10, event.getScreenY() + 10);
		});
		tile.setOnMouseExited(event -> {
			tooltip.hide();
		});
	}
}
