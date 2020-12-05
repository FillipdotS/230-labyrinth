package source.labyrinth.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import source.labyrinth.MessageOfTheDay;
import source.labyrinth.ProfileManager;

import java.io.IOException;

public class MainMenuController extends Application {
	@FXML private Text motd;

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("MainMenuController created.");
		Parent root = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Main Menu");
		primaryStage.show();

		// Setup profiles
		ProfileManager.performSetup();
	}

	@FXML
	private void initialize() {
		// Show motd
		motd.setText("Message of the day: " + MessageOfTheDay.getMessageOfTheDay());
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

	@FXML
	public void goToLeaderboards(ActionEvent event) {
		System.out.println("Going to leaderboards...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/leaderboards.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Level Select");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void goToProfileMenu(ActionEvent event) {
		System.out.println("Going to profile menu...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/profile_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Profile Menu");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void exitGame() {
		Platform.exit();
	}
}
