package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * @author Max
 * menu to select existing level to edit
 * reused functions to get level file
 * In progress: make a real editor that can read level and edit it
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
	private TextArea detail;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		renderLevels();
		System.out.println("Created LevelMenuController");
		edit.setDisable(true);
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
				if (selectedHBox != null) {
					selectedHBox.setStyle("-fx-border-color: black");
				}
				selectedHBox = levelHBox;
				selectedLevel = value.substring(0, value.length() - 4);
				System.out.println(selectedLevel);


				File levelsFiles = new File("./source/resources/custom_levels");
				for (File f : Objects.requireNonNull(levelsFiles.listFiles())) {
					if (value.equals(f.getName()))
						detail.setText("file location:\n"+f.getAbsolutePath());
				}


				edit.setDisable(false);
				levelHBox.setStyle("-fx-border-color: black;-fx-background-color: #c4ffd5;");
				//renderLeaderBoard();
			});
			vboxLevels.getChildren().addAll(levelHBox);
		});


	}


	/**
	 * from level menu
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
	 * go to level editor with exist level
	 *
	 * @param event
	 */
	@FXML
	public void goToLevelEditorExist(ActionEvent event) {
		System.out.println("Exist Board Editor");
		LevelEditorController loadExist = new LevelEditorController();
		System.out.println("Loading an exist board to edit...");
		loadExist.setNextFileToLoad(selectedLevel);

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


}
