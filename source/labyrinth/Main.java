package source.labyrinth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * author Max
 * simply create main to run MainMenu
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("MainMenuController created.");
		Parent root = FXMLLoader.load(getClass().getResource("../resources/scenes/main_menu.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Main Menu");
		primaryStage.show();

		// Setup profiles
		ProfileManager.performSetup();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
