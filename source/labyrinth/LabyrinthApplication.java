package source.labyrinth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Arrays;

public class LabyrinthApplication extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("../resources/scenes/board.fxml"));

		Scene scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.show();

		// temp code, make a board and show it
		Board board = new Board(7, 3);
		Pane p = (Pane) scene.lookup("#putBoardHere");
		GridPane g = board.renderBoard(55);
		p.getChildren().add(g);

		FloorTile testTile = new FloorTile(0, FloorTile.TileType.CORNER);
		board.insertFloorTile(testTile, 0, 0);
		board.insertFloorTile(testTile, 2, 0);
		board.insertFloorTile(testTile, 3, 0);
		board.insertFloorTile(testTile, 1, 0);
		board.insertFloorTile(testTile, 2, 6);
		board.insertFloorTile(testTile, 3, 2);
		p.getChildren().clear();
		p.getChildren().add(board.renderBoard(55));

		Boolean[][] b = board.getInsertablePositions();
		System.out.println("This board can be inserted into these columns: ");
		System.out.println(Arrays.toString(b[0]));
		System.out.println("And inserted into these rows: ");
		for (int i = 0; i < b[1].length; i++) {
			System.out.println(b[1][i]);
		}

		// ProfileManager test
		ProfileManager pm = new ProfileManager();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
