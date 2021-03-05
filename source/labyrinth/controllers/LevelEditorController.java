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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * LevelEditorController is used when editing a game board.
 *
 * @author Fillip Serov
 */
public class LevelEditorController implements Initializable {
    private static String nextFileToLoad; // null if completely new board, name of custom board file otherwise
    private static int tileRenderSize = 64;

    private int[][] playerLocations = new int[0][0];
    private int playerCount = 1;
    private String tileTypeHelp = "";

    @FXML
    private VBox boardContainer;
    @FXML
    private HBox bottomContainer;
    @FXML
    private ToggleGroup editingState;

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

        if (nextFileToLoad != null) {
            LevelData ld = LevelIO.readDataFile("source/resources/custom_levels/" + nextFileToLoad + ".txt");
            board = ld.getBoard();
        } else {
            board = new Board(0, 0);
        }

        editingState.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            RadioButton selected = (RadioButton) editingState.getSelectedToggle();
            currentState = EditingState.valueOf(selected.getId());
            System.out.println("Changed currentState to: " + currentState);
            updateBottomContainer();
            renderBoard();//have to update tooltip when change mode
        }));

        currentState = EditingState.BOARD_SIZE;
        updateBottomContainer();


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
     * Select the width and the height of the board that you want to create
     * Generates a board with 4 fixed corner tiles and a goal tile
     * You can always go to the default board by pressing the default button
     */
    private void selectBoardSize() {
        TextField width = new TextField();
        width.setPromptText("Enter width.");
        bottomContainer.getChildren().add(width);

        TextField height = new TextField();
        height.setPromptText("Enter height.");
        bottomContainer.getChildren().add(height);

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

            playerLocations = new int[newWidth][newHeight];
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
            renderBoard();
        });


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
                renderBoard();
            }
        });

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
     * Shows the 4 floor tiles (and a fifth empty one) that you can click and place. Assumed that
     * bottomContainer is cleared out already before this method is called.
     */
    private void showFixedTileControls() {
        ArrayList<FloorTile> tiles = new ArrayList<FloorTile>();
        for (FloorTile.FloorType type : FloorTile.FloorType.values()) {
            tiles.add(new FloorTile(2, type, true));
        }
        int TILE_RENDER_SIZE = 64;
        int tileType = 0;
        for (FloorTile tile : tiles) {
            StackPane stackTile = tile.renderTile(TILE_RENDER_SIZE);
            if (tileType == 0) {
                tileTypeHelp = "Straight Tile:";
            }
            if (tileType == 1) {
                tileTypeHelp = "Corner Tile:";
            }
            if (tileType == 2) {
                tileTypeHelp = "T Tile:";
            }
            if (tileType == 3) {
                tileTypeHelp = "Cross Tile:";
            }
            final Tooltip TileTip = new Tooltip(tileTypeHelp + "\nLeft click to select the tile");
            tileType++;
            TileTip.setStyle("-fx-font-size: 16");
            showToolTip(stackTile, TileTip);
            stackTile.setOnMouseClicked(event -> {
                setSelectedFloorTile(new FloorTile(2, tile.getFloorType(), true));
                updateBottomContainer();
            });
            if ((selectedFloorTile != null) && (tile.getFloorType() == selectedFloorTile.getFloorType())) {
                ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", TILE_RENDER_SIZE, TILE_RENDER_SIZE, false, false));
                chosen.setOpacity(0.5);
                stackTile.getChildren().add(chosen);
            }
            bottomContainer.getChildren().add(stackTile);
        }


        Image img = new Image("source/resources/img/tile_none.png", TILE_RENDER_SIZE, TILE_RENDER_SIZE, false, false);
        StackPane stack = new StackPane(new ImageView(img));
        final Tooltip clearTip = new Tooltip("Empty Tile:\n" + "Clear the selected tile on board\n" + "Left click to select the tile");
        clearTip.setStyle("-fx-font-size: 16");
        showToolTip(stack, clearTip);
        stack.setOnMouseClicked(event -> {
            setSelectedFloorTile(null);
            updateBottomContainer();
        });
        if (selectedFloorTile == null) {
            ImageView chosen = new ImageView(new Image("source/resources/img/chosen_one.png", TILE_RENDER_SIZE, TILE_RENDER_SIZE, false, false));
            chosen.setOpacity(0.5);
            stack.getChildren().add(chosen);
        }
        bottomContainer.getChildren().add(stack);
    }

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
        //Check if there is more than 4 player
        if (playerCount < 5) {
            playerLocations[x][y] = playerCount;
            playerCount++;
            System.out.println("Player placed at " + x + "," + y);
        } else {
            System.out.println("Maximum Player!");
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
        if (playerLocations[x][y] != 0) {
            playerLocations[x][y] = 0;
            playerCount--;
            System.out.println("Player removed at " + x + "," + y);
        } else {
            System.out.println("There is no player at that location!");
        }
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
                    if (currentState.equals(EditingState.FIXED_TILES)) {
                        final Tooltip TileTip = new Tooltip("Left click - replace tile\n" + "Right click - delete tile\n" + "Middle click - rotate");
                        TileTip.setStyle("-fx-font-size: 16");
                        showToolTip(stack, TileTip);
                    }

                } else {
                    Image img = new Image("source/resources/img/tile_none.png", tileRenderSize, tileRenderSize, false, false);
                    ImageView iv = new ImageView(img);
                    stack = new StackPane(iv);
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
    private void fileWriter() throws IOException {
        File filename = new File("source\\resources\\custom_levels");
        FileWriter writer = new FileWriter(filename);
        int numOfFixedTiles = 5;
        int[][] tempPlayerPos = new int[0][0];
        int[][] tempPlayerPos2 = new int[6][2];
        int[][] tempPlayerPos3 = new int[6][0];
        int[][] tempPlayerPos4 = new int[0][2];

        writer.write(board.getWidth() + "," + board.getHeight());
        writer.write(numOfFixedTiles);

        writer.write(board.getTileAt(0, 0) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
        writer.write(board.getTileAt(6, 0) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
        writer.write(board.getTileAt(0, 2) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
        writer.write(board.getTileAt(6, 2) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());
        writer.write(board.getTileAt(0, 2) + "," + selectedFloorTile.getFloorType() + "," + selectedFloorTile.getOrientation());

        writer.write(String.valueOf(tempPlayerPos));
        writer.write(String.valueOf(tempPlayerPos2));
        writer.write(String.valueOf(tempPlayerPos3));
        writer.write(String.valueOf(tempPlayerPos4));
        //Not sure how to get the remaining tiles in silkbag.
        // writer.write(SilkBag.getEntireBag());
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
