package source.labyrinth.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import source.labyrinth.Profile;
import source.labyrinth.ProfileManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileMenuController implements Initializable {
	@FXML private TextField newProfileName;
	@FXML private TableView<Profile> tableView;
	@FXML private TableColumn<Profile, String> nameCol;
	@FXML private TableColumn<Profile, Integer> totalCol;
	@FXML private TableColumn<Profile, Integer> winCol;
	@FXML private TableColumn<Profile, Integer> lossCol;

	private ProfileManager pm = new ProfileManager();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created ProfileMenuController");

		nameCol.setCellValueFactory(new PropertyValueFactory<Profile, String>("name"));
		totalCol.setCellValueFactory(new PropertyValueFactory<Profile, Integer>("totalPlayed"));
		winCol.setCellValueFactory(new PropertyValueFactory<Profile, Integer>("wins"));
		lossCol.setCellValueFactory(new PropertyValueFactory<Profile, Integer>("losses"));

		tableView.getItems().setAll(pm.getProfiles());
	}

	@FXML
	public void makeNewProfile() {
		System.out.println("Attempting to make new profile...");
		String newName = newProfileName.getText();
		String potentialError = null;

		if (newName.isEmpty()) {
			System.out.println("No name was given.");
			potentialError = "Profile name cannot be empty!";
		} else if (newName.isEmpty()) {
			System.out.println("No name was given.");
			potentialError = "Profile name cannot be empty!";
		} else if (newName.trim().isEmpty()) {
			System.out.println("Profile name with just spaces");
			potentialError = "Profile name must not be only spaces";
		}
		else if (!pm.createNewProfile(newName)) {
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
		tableView.getItems().setAll(pm.getProfiles());
	}

	@FXML
	public void returnToMainMenu(ActionEvent event) {
		System.out.println("Going back to main menu...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
