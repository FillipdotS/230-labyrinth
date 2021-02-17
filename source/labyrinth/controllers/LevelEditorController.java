package source.labyrinth.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import source.labyrinth.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * LevelEditorController is used when editing a game board.
 *
 * @author Fillip Serov
 */
public class LevelEditorController implements Initializable {
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

	private Board board;
	private EditingState currentState;
	private FloorTile selectedFloorTile; // A copy of this is placed onto the board

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LevelEditorController");

		// temp variable
		String levelName = "example_level";

		// temp variable
		selectedFloorTile = new FloorTile(2, FloorTile.FloorType.TSHAPE,true);

		LevelData ld = LevelIO.readDataFile("source/resources/levels/" + levelName + ".txt");
		board = ld.getBoard();

		editingState.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
			RadioButton selected = (RadioButton) editingState.getSelectedToggle();
			currentState = EditingState.valueOf(selected.getId());
			System.out.println("Changed currentState to: " + currentState);
			updateBottomContainer();
		}));

		currentState = EditingState.FIXED_TILES;
		updateBottomContainer();

		renderBoard();
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
				bottomContainer.getChildren().add(new Text("Board size stuff"));
				break;
			case FIXED_TILES:
				showFixedTileControls();
				break;
			case SILK_BAG:
				bottomContainer.getChildren().add(new Text("Silk bag stuff"));
				break;
			case PLAYERS:
				bottomContainer.getChildren().add(new Text("Player stuff"));
				break;
			case SAVE:
				bottomContainer.getChildren().add(new Text("Save stuff"));
				break;
		}
	}

	/**
	 * Shows the 4 floor tiles (and a fifth empty one) that you can click and place. Assumed that
	 * bottomContainer is cleared out already before this method is called.
	 */
	private void showFixedTileControls() {
		// TODO: Actually implement
		ArrayList<FloorTile> tiles = new ArrayList<FloorTile>();
		for (FloorTile.FloorType type:FloorTile.FloorType.values()) {
			tiles.add(new FloorTile(2,type,true));
		}
		for (FloorTile tile: tiles) {
			StackPane stackTile = tile.renderTile(tileRenderSize);
			stackTile.setOnMouseClicked(event -> {
				setSelectedFloorTile(new FloorTile(2,tile.getFloorType(),true));
				updateBottomContainer();
			});
			if (tile.getFloorType() == selectedFloorTile.getFloorType()) {
				ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", tileRenderSize, tileRenderSize, false, false));
				chosen.setOpacity(0.5);
				stackTile.getChildren().add(chosen);
			}
			bottomContainer.getChildren().add(stackTile);
		}

		
	}

	private void setSelectedFloorTile(FloorTile tile){
		selectedFloorTile = tile;
	}

	/**
	 * Places the currently selected tile at (x, y)
	 *
	 * @param x X-coord
	 * @param y Y-coord
	 */
	private void placeFixedTileAt(int x, int y) {
		// Deep copy the floor tile, otherwise rotating one will rotate them all
		FloorTile copy = new FloorTile(selectedFloorTile.getOrientation(), selectedFloorTile.getFloorType(),selectedFloorTile.getFixed());
		board.setTileAt(copy, x, y);
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
						// Place player at
						break;
					case SECONDARY:
						// Delete player at
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
				} else {
					Image img = new Image("source/resources/img/tile_none.png", tileRenderSize, tileRenderSize, false, false);
					ImageView iv = new ImageView(img);
					stack = new StackPane(iv);
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
}
