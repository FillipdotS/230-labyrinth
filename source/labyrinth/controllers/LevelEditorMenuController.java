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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the editor menu, where the user can edit custom boards, create new ones, and delete them.
 * @author Max
 */
public class LevelEditorMenuController implements Initializable {
	private static String selectedLevel;
	private static HBox selectedHBox;
	private static ArrayList<String> fileLocation = new ArrayList<>();


	@FXML
	private VBox vboxLevels;
	@FXML
	private Button edit;
	@FXML
	private Button deleteLv;
	@FXML
	private TextArea detail;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// In case this is after we have already once loaded a custom level in this session
		LevelEditorController.setNextFileToLoad(null);


		System.out.println("Created LevelMenuController");
		edit.setDisable(true);
		deleteLv.setDisable(true);
		detail.setText("");
		renderLevels();
	}

	/**
	 * from level menu
	 * renders level list in menu
	 */
	private void renderLevels() {
		vboxLevels.getChildren().clear();
		getLevels().forEach((value) -> {
			HBox levelHBox = new HBox(new Text(value.substring(0, value.length() - 4)));
			levelHBox.setPrefHeight(30);
			levelHBox.setAlignment(Pos.CENTER_LEFT);
			levelHBox.setStyle("-fx-border-color: black");
			levelHBox.setOnMouseClicked(event -> {
				edit.setDisable(false);
				deleteLv.setDisable(false);

				if (selectedHBox != null) {
					selectedHBox.setStyle("-fx-border-color: black");
				}

				selectedHBox = levelHBox;
				selectedLevel = value.substring(0, value.length() - 4);
				System.out.println(selectedLevel);


				File levelsFiles = new File("./source/resources/custom_levels");
				for (File f : Objects.requireNonNull(levelsFiles.listFiles())) {
					if (value.equals(f.getName()))
						detail.setText("Level name:\n" + f.getName() + "\n\nLevel location:\n" + f.getAbsolutePath());
				}


				levelHBox.setStyle("-fx-border-color: black;-fx-background-color: #00FFFF;");
				//renderLeaderBoard();
			});
			vboxLevels.getChildren().addAll(levelHBox);
		});


	}


	/**
	 * from level menu
	 * @return ArrayList of strings (which are level names)
	 */
	private ArrayList<String> getLevels() {
		File levelsFiles = new File("./source/resources/custom_levels");
		ArrayList<String> levels = new ArrayList<>();

		for (File f : Objects.requireNonNull(levelsFiles.listFiles())) {
			levels.add(f.getName());
		}
		return levels;
	}

	/**
	 * Delete the currently selected level.
	 */
	@FXML
	public void deleteLevel() {
		if (selectedHBox != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Delete Level");
			alert.setHeaderText("Warning !");
			alert.setContentText("Are you sure you want to delete this level?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				File delFile = new File("./source/resources/custom_levels/" + selectedLevel + ".txt");
				delFile.delete();

				// avoid to load a deleted file
				selectedLevel = null;
				selectedHBox = null;
				detail.setText("");

				deleteLv.setDisable(true);
				edit.setDisable(true);
				renderLevels();//refresh the saveData list

				Alert deleted = new Alert(Alert.AlertType.INFORMATION);
				deleted.setTitle("Delete Level");
				deleted.setHeaderText("Level deleted");
				deleted.showAndWait();
				alert.setOnCloseRequest(this::refresh);

			} else {
				System.out.println("Delete Cancelled");
			}
		} else {
			System.out.println("Error Selection");
		}
	}


	/**
	 * go to level editor with exist level
	 *
	 * @param event Event to grab scene from
	 */
	@FXML
	public void goToLevelEditorExist(ActionEvent event) {
		System.out.println("Exist Board Editor");
		System.out.println("Loading an exist board to edit...");
		LevelEditorController.setNextFileToLoad(selectedLevel);

		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Board Editor");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		edit.setDisable(true);
	}

	/**
	 * go to level editor
	 *
	 * @param event Click event to get window from
	 */
	@FXML
	public void goToLevelEditorNew(ActionEvent event) {
		System.out.println("Going to level editor...");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Board Editor");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@FXML
	public void returnToMainMenu(ActionEvent event) {
		System.out.println("Main Menu");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Main Menu");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void refresh(DialogEvent event) {
		System.out.println("Reload Level Editor");
		try {
			Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor_menu.fxml"));
			Scene profileMenuScene = new Scene(profileMenuParent);
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			window.setScene(profileMenuScene);
			window.setTitle("Refreshed");
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
