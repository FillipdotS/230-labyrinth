package source.labyrinth.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import source.labyrinth.MessageOfTheDay;
import source.labyrinth.ProfileManager;

import java.io.IOException;

/**
 * JavaFX controller for main_menu.fxml, shows controls to go to different parts of the game.
 * @author Fillip Serov
 */

public class MainMenuController {//extends Application (original)
	@FXML private TextArea motd;

/*	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override//testing on Main
	public void start(Stage primaryStage) throws Exception {
		System.out.println("MainMenuController created.");
		Parent root = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Main Menu");
		primaryStage.show();

		// Setup profiles
		ProfileManager.performSetup();
	}*/

	/**
	 * Go to the level menu
	 * @param event Click event to get window from
	 */
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
	 * Go to the profile menu
	 * @param event Click event to get window from
	 */
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

	/**
	 * Go to the save menu
	 * @param event Click event to get window from
	 */
	@FXML
	public void goToSaveMenu(ActionEvent event) {
		System.out.println("Going to save menu...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/save_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Save Menu");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Go to the editor menu
	 * @param event Click event to get window from
	 */
	@FXML
	public void goToLevelEditorMenu(ActionEvent event) {
		System.out.println("Going to level editor menu...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Level Editor Menu");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exits the program
	 */
	@FXML
	public void exitGame() {
		Platform.exit();
	}

	@FXML
	private void initialize() {
		motd.setText(MessageOfTheDay.getMessageOfTheDay());
	}
}
