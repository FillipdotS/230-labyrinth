package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import source.labyrinth.Profile;
import source.labyrinth.ProfileManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * JavaFX controller for the profile menu that deals with viewing, deleting and creating profiles.
 */
public class ProfileMenuController implements Initializable {
	@FXML private TextField newProfileName;
	@FXML private TableView<Profile> tableView;
	@FXML private TableColumn<Profile, String> nameCol;
	@FXML private TableColumn<Profile, Integer> totalCol;
	@FXML private TableColumn<Profile, Integer> winCol;
	@FXML private TableColumn<Profile, Integer> lossCol;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created ProfileMenuController");

		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPlayed"));
		winCol.setCellValueFactory(new PropertyValueFactory<>("wins"));
		lossCol.setCellValueFactory(new PropertyValueFactory<>("losses"));

		tableView.getItems().setAll(ProfileManager.getProfiles());
	}

	/**
	 * Delete the currently selected profile (which is determined by the table).
	 */
	@FXML
	public void deleteProfile() {
		Profile toDelete = tableView.getSelectionModel().getSelectedItem();
		if (toDelete != null) {
			// Allow user to confirm their deletion
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Profile Deletion");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you would like to delete the profile '" + toDelete.getName() + "' forever?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				ProfileManager.deleteProfile(toDelete);
				tableView.getItems().setAll(ProfileManager.getProfiles());
			}
		}
	}

	/**
	 * Make a new profile with the inputted name
	 */
	@FXML
	public void makeNewProfile() {
		System.out.println("Attempting to make new profile...");
		String newName = newProfileName.getText();
		String potentialError = null;

		if (newName.isEmpty()) {
			System.out.println("No name was given.");
			potentialError = "Profile name cannot be empty!";
		} else if (newName.trim().isEmpty()) {
			System.out.println("Profile name with just spaces");
			potentialError = "Profile name must not be only spaces";
		}
		else if (!ProfileManager.createNewProfile(newName)) {
			System.out.println("Name is already in use.");
			potentialError = "A profile with this name already exists";
		}

		if (potentialError != null) {
			System.out.println("Name is already in use.");
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText(potentialError);
			alert.showAndWait();
			return;
		}

		System.out.println("Created profile with " + newName);
		newProfileName.clear();
		tableView.getItems().setAll(ProfileManager.getProfiles());
	}

	/**
	 * Goes to the main menu
	 * @param event Click event to get current window
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
