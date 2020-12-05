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
import source.labyrinth.Profile;
import source.labyrinth.ProfileManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * LevelMenuController let user choose level number of players and associated profiles
 * @author Erik Miller
 */

public class LevelMenuController implements Initializable {
	@FXML private VBox vboxLevels;
	@FXML private VBox vboxPlayers;
	@FXML private Button addPlayerButton;
	@FXML private Button removePlayerButton;

	private static String selectedLevel;
	private static HBox selectedHBox;
	private static int numberOfPlayers =  2;
	private final static ArrayList<String> profilesChosen = new ArrayList<>();
	private static ArrayList<String> profileNames;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		renderLevels();
		renderPlayersChoiceBox();

		addPlayerButton.setOnMouseClicked(event -> {
			numberOfPlayers = numberOfPlayers==4?4:numberOfPlayers+1;
			renderPlayersChoiceBox();
		});
		removePlayerButton.setOnMouseClicked(event -> {
			numberOfPlayers = numberOfPlayers==2?2:numberOfPlayers-1;
			if(profilesChosen.size() > numberOfPlayers) profilesChosen.remove(numberOfPlayers);
			renderPlayersChoiceBox();
		});

		System.out.println("Created LevelMenuController");
	}

	/**
	 * renders level list in menu
	 */
	private void renderLevels() {
		vboxLevels.getChildren().clear();
		getLevels().forEach((value) -> {
			HBox levelHBox = new HBox(new Text(value.substring(0,value.length()-4)));
			levelHBox.setPrefHeight(30);
			levelHBox.setAlignment(Pos.CENTER_LEFT);
			levelHBox.setStyle("-fx-border-color: black");
			levelHBox.setOnMouseClicked(event -> {
				System.out.println(value);
				if (selectedHBox != null) {
					selectedHBox.setStyle("-fx-border-color: black");
				}
				selectedHBox = levelHBox;
				selectedLevel = value;
				levelHBox.setStyle("-fx-border-color: black;-fx-background-color: #c4ffd5;");
			});
			vboxLevels.getChildren().addAll(levelHBox);
		});
	}

	/**
	 * renders player profile choice in menu
	 */
	private void renderPlayersChoiceBox(){
		vboxPlayers.getChildren().clear();

		ArrayList<Profile> profiles = ProfileManager.getProfiles();
		profileNames = new ArrayList<>();
		profiles.forEach(profile -> profileNames.add(profile.getName()));

		for (int i = 0; i < numberOfPlayers; i++) {
			ChoiceBox<String> pChoiceBox = new ChoiceBox<>();
			pChoiceBox.setPrefWidth(250);
			pChoiceBox.getItems().addAll(profileNames);

			profilesChosen.forEach(prof -> pChoiceBox.getItems().remove(prof));
			if (profilesChosen.size() > i) {
				pChoiceBox.getItems().addAll(profilesChosen.get(i));
			}
			if (profilesChosen.size() > i) {
				pChoiceBox.getSelectionModel().select(profilesChosen.get(i));
			}

			pChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
					if (!oldValue.equals(-1)) {
						profilesChosen.remove(pChoiceBox.getItems().get((oldValue.intValue())));
					}
					profilesChosen.add(pChoiceBox.getItems().get((newValue.intValue())));
					renderPlayersChoiceBox();
				}
			);
			vboxPlayers.getChildren().addAll(pChoiceBox);
		}

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

	/**
	 * reeds level names fromm files
	 * @return level names as string
	 */
	private ArrayList<String> getLevels() {
		File levelsFiles = new File("./source/resources/levels");
		ArrayList<String> levels = new ArrayList<>();
		for (File f : Objects.requireNonNull(levelsFiles.listFiles())) {
			levels.add(f.getName());
			}
		return levels;
	}

	/**
	 * starts game
	 * @param event click on button
	 */
	@FXML
	public void playGame(ActionEvent event) {
		if (selectedLevel != null) {
			System.out.println("Going to board ...");
			String[] prof = new String[numberOfPlayers];
			for (int i = 0; i < prof.length; i++) {
				if (profilesChosen.size() > i) prof[i] = profilesChosen.get(i);
			}
			LevelController.setNextLevelProfiles(prof);
			LevelController.setNextLevelToLoad(selectedLevel);

			System.out.println("level: " + selectedLevel);
			for (String s : prof) {
				System.out.println("player " + s);
			}

			try {
				Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level.fxml"));
				Scene profileMenuScene = new Scene(profileMenuParent);
				Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
				window.setScene(profileMenuScene);
				window.setTitle("Level Select");
				window.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Choose a level!");
			alert.showAndWait();
		}
	}
}
