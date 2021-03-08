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
 */
public class LevelEditorController implements Initializable {
	private static String nextFileToLoad; // null if completely new board, name of custom board file otherwise
	private static int tileRenderSize = 64;

	@FXML
	private VBox boardContainer;
	@FXML
	private HBox bottomContainer;
	@FXML
	private ToggleGroup editingState;

	private enum EditingState {
		BOARD_SIZE,
		FIXED_TILES,
		SILK_BAG,
		PLAYERS,
		SAVE
	}

	private int[][] boardLocations = new int[0][0];
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
			nextFileToLoad = null;//have to reset each time, otherwise keep loading the same level
		} else {
			board = new Board(0, 0);
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
	 * -> Enough tiles in silkbag for the board
	 * -> There is at least one goal tile
	 * -> That four player positions have been selected
	 *
	 * @return Alert - Returns an alert if conditions not me; if null then program continues
	 */
	private Alert validateLevel() {
		String errorTitle = "The following errors have occured in your board settings:\n";
		String errorLog = "";
		int silkBagAccumulator;
		int boardArea = board.getHeight() * board.getWidth();
		int goalCount = silkbagAmounts.get("GOAL");
		silkBagAccumulator = silkbagAmounts.get("STRAIGHT") + silkbagAmounts.get("TSHAPE") +
				silkbagAmounts.get("CORNER") + silkbagAmounts.get("GOAL") + silkbagAmounts.get("ICE")
				+ silkbagAmounts.get("FIRE") + silkbagAmounts.get("DOUBLEMOVE") + silkbagAmounts.get("BACKTRACK");

		if (silkBagAccumulator < boardArea) {
			errorLog += "-> There are not enough tiles in the silk bag, " +
					"please increase this to atleast the area of the board\n";
		}

		if (goalCount < 1) {
			errorLog += "-> There has to be AT LEAST ONE goal tile, please add a goal tile\n";
		}

		if (getPlayerLocations().length != 4) {
			errorLog += "-> All player positions have to be defined, please define them\n";
		}

		if (errorLog == null || errorLog.isEmpty() || errorLog.contains(" ")) {
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
		System.out.println(fileName);
		System.out.println("Call validate");
		Alert errorDialog = validateFileName(fileName);
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
					fileWriter(filePath);
				} else {
					overwriteDialog.close();
				}
			} else {
				fileWriter(filePath);
			}
		}
	}

	@FXML
	public void saveChanges() throws IOException {//removed unused Action event parameter, unexpected problem may happen
		TextInputDialog textDialog = new TextInputDialog();
		textDialog.setTitle("Save your Level");
		textDialog.setHeaderText("Give Your Level A Name");

		Optional<String> result = textDialog.showAndWait();
		if (result.isPresent()) {//check if user pressed ok button
			textDialogOk(textDialog);
		}
		/* NOT TOO SURE IF IT WORKS, ISSUE WITH CANCEL BUTTON OF DIALOG
		Button okButton = (Button) textDialog.getDialogPane().lookupButton(ButtonType.OK);
		Button cancelButton = (Button) textDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		okButton.addEventFilter(ActionEvent.ACTION, e ->
				{
					try {
						System.out.println("Call method");
						textDialogOk(textDialog);
					} catch (IOException ioException) {
						System.out.println("Error with textDialog to textDialogOk method");
					}
				}
		);*/

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
				Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/editor_menu.fxml"));
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
				bottomContainer.getChildren().add(new Text("Left click to put player"));
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
			renderBoard();
		});

		Button widthDecreaseButton = new Button("-");
		widthDecreaseButton.setOnAction(event -> {
			board.changeSize(board.getWidth() - 1, board.getHeight());
			renderBoard();
		});
		//helper text
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
			renderBoard();
		});

		Button heightDecreaseButton = new Button("-");
		heightDecreaseButton.setOnAction(event -> {
			board.changeSize(board.getWidth(), board.getHeight() - 1);
			renderBoard();
		});
		//helper text
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

			boardLocations = new int[newWidth][newHeight];
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
				boardLocations[newWidth / 2][newHeight / 2] = 2;
			}

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

	private void addEvent() {
		Scene scene = boardContainer.getScene();
		scene.setOnKeyPressed((key) -> {
			if (key.getCode() == KeyCode.A) {
				System.out.println("AAAAAAAAAAAAA");
				rotateSelectedFloorTile(-1);
			}
			if (key.getCode() == KeyCode.D) {
				System.out.println("DDDDDDDDDDDDDDD");
				rotateSelectedFloorTile(1);
			}
		});
	}

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

		ArrayList<FloorTile> tiles = new ArrayList<FloorTile>();
		for (FloorTile.FloorType type : FloorTile.FloorType.values()) {
			tiles.add(new FloorTile(0, type, true));
		}

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
			final Tooltip tileTip = new Tooltip(tileTypeHelp + "\nLeft click to select the tile\nClick an empty tile on board to place it\nClick an exist tile on board to replace with it.");
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

			addEvent();

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
				tileHelp2 = "\n Player win when reach this tiles";
			}
			if (tileTypeName.equals("ICE")) {
				tileHelp1 = "Ice Tile";
				tileHelp2 = "\nFreeze a 3 x 3 area with a selected tiles as centre\nThe rows and columns that being affected will not able to move" +
						"\n Players are still able to travel on these frozen tiles\n The frozen tiles melts away at the start of your next turn.";
			}
			if (tileTypeName.equals("FIRE")) {
				tileHelp1 = "Fire Tile";
				tileHelp2 = "\nBurn a 3 x 3 area with a selected tiles as centre\nPlayers are not able to end turn on a burning tile.\n Fire will be extinguished after your turn end.";
			}
			if (tileTypeName.equals("DOUBLEMOVE")) {
				tileHelp1 = "Double Move Tile";
				tileHelp2 = "\n Apply to move self twice";
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
			numField.setOnAction(event -> {
				try {
					int newValue = Integer.parseInt(numField.getText());
					newValue = newValue > -1 ? newValue : 0; // If user put negative number, make it 0

					silkbagAmounts.put(tileTypeName, newValue);
					numField.setText(String.valueOf(newValue)); // User could have put "025" or something similar
				} catch (NumberFormatException e) {
					numField.setText(silkbagAmounts.get(tileTypeName).toString());
				}
			});
			//helper text
			final Tooltip numTile = new Tooltip("Input an integer to set the number of " + tileHelp1 + " in silk bag.");
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
	 * Places the player at the selected tile(x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void placePlayerAt(int x, int y) {
		//Check how many player have been add
		int playerCount = 0;
		for (int i = 0; i < boardLocations[0].length; i++) {
			for (int j = 0; j < boardLocations[1].length; j++) {
				if (boardLocations[i][j] == 1) {
					playerCount++;
				}
			}
		}
		//Check if there is a player or it is a Goal tile
		if (boardLocations[x][y] == 1) {
			System.out.println("There is already a Player in that location");
		} else if (boardLocations[x][y] == 2) {
			System.out.println("That was a Goal Tile.");
		} else {
			//Check if there is more than 4 player
			if (playerCount < 4) {
				boardLocations[x][y] = 1;
				System.out.println("Player placed at " + x + "," + y);
			} else {
				System.out.println("Maximum Player!");
			}
		}
	}

	/**
	 * Remove the player at the selected tile(x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void removePlayerAt(int x, int y) {
		//Check if there is any player
		if (boardLocations[x][y] == 1) {
			//playerCount =  boardLocations[x][y];
			boardLocations[x][y] = 0;
			System.out.println("Player removed at " + x + "," + y);
		} else {
			System.out.println("There is no player at that location!");
		}
	}

	/**
	 * Get all the player location into a string array
	 */
	private String[] getPlayerLocations() {
		String[] output = new String[4];
		int k = 0;
		for (int x = 0; x < boardLocations[0].length; x++) {
			for (int y = 0; y < boardLocations[1].length; y++) {
				if (boardLocations[x][y] == 1) {
					output[k] = x + "," + y;
				}
			}
		}
		return output;
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
			if (copy.getFloorType() == FloorTile.FloorType.GOAL) {
				boardLocations[x][y] = 2;
			}
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
		if (boardLocations[x][y] == 2) {
			boardLocations[x][y] = 0;
		}
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


				// TODO: Render player positions onto the grid here

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

		boardContainer.getChildren().clear();
		boardContainer.getChildren().add(renderedBoard);
	}

	@FXML
	private void fileWriter(File filename) throws IOException {
		FileWriter writer = new FileWriter(filename);
		int numOfFixedTiles = 0;
		int posInX = 0;
		int posInY = 0;
		for (int i = 0; i < board.getHeight(); i++) {
			for (int j = 0; j < board.getWidth(); j++) {
				Tile currentTile = board.getTileAt(i, j);
				if (currentTile != null) {
					posInY = i;
					posInX = j;
					numOfFixedTiles += 1;
				}
			}
		}
		Alert errorDialog = validateLevel();
		if (errorDialog != null) {
			errorDialog.showAndWait();
		} else {
			writer.write(board.getWidth() + "," + board.getHeight());
			writer.write(numOfFixedTiles);

			writer.write(board.getTileAt(posInX, posInY) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
			writer.write(board.getTileAt(posInX, posInY) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
			writer.write(board.getTileAt(posInX, posInY) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
			writer.write(board.getTileAt(posInX, posInY) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
			writer.write(board.getTileAt(posInX, posInY) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());

			writer.write(String.valueOf(getPlayerLocations()));

			writer.write(silkbagAmounts.get("STRAIGHT") + "," + "Straight: ");
			writer.write(silkbagAmounts.get("TSHAPE") + "," + "TShape: ");
			writer.write(silkbagAmounts.get("CORNER") + "," + "Corner: ");
			writer.write(silkbagAmounts.get("GOAL") + "," + "Goal: ");
			writer.write(silkbagAmounts.get("ICE") + "," + "ICE: ");
			writer.write(silkbagAmounts.get("FIRE") + "," + "Fire: ");
			writer.write(silkbagAmounts.get("DOUBLEMOVE") + "," + "DoubleMove: ");
			writer.write(silkbagAmounts.get("BACKTRACK") + "," + "BackTrack: ");
		}


	}


	/**
	 * replace the original install method from tooltips to remove delay time
	 *
	 * @param tile
	 * @param tooltip
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
