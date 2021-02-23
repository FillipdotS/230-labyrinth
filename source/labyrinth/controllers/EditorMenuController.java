package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * EditorMenuController is used in the level editor menu where the user picks a game board to edit,
 * or to create a new one.
 * @author Fillip Serov
 */
public class EditorMenuController implements Initializable {
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created EditorMenuController");
	}

	/**
	 * Go to the level editor
	 * @param event Click event to get window from
	 */
	@FXML
	public void goToLevelEditor(ActionEvent event) {
		System.out.println("Going to level editor...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor.fxml"));
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
	public void goToBoardEditorExist(ActionEvent event) {
		System.out.println("Going to board editor (exist)...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/exist_board_select.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Exist Board Editor");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@FXML
	public void goToBoardEditorNew(ActionEvent event) {
		System.out.println("Going to board editor (new)...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/board_editor_new.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("New Board Editor");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}











	/**
	 * Return to the main menu screen
	 * @param event Event click to find current window
	 */
	@FXML
	public void returnToMainMenu(ActionEvent event) {
		System.out.println("Going back to main menu...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Main Menu");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
