package source.labyrinth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LabyrinthApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("../resources/scenes/board.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

        // temp code, make a board and show it
        Board board = new Board(7, 7);
        Pane p = (Pane) scene.lookup("#putBoardHere");
        GridPane g = board.renderBoard();
        p.getChildren().add(g);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
