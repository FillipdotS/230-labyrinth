package source.labyrinth.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
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
import java.util.ResourceBundle;

public class LevelMenuController implements Initializable {
	@FXML private VBox vboxLevels;
	@FXML private VBox vboxPlayes;

	public static String selectedLevel;
	public static HBox selectedHBox;
	public static int numberOfPlayers =  2;
	public static String[] profilesChosen;
	public static ArrayList<Profile> profiles;
	public static ArrayList<String> profileNames;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getLevels().forEach((value) -> {
			HBox levelHBox = new HBox(new Text(value.substring(0,value.length()-4)));
			levelHBox.setPrefHeight(30);
			levelHBox.setAlignment(Pos.CENTER_LEFT);
			levelHBox.setStyle("-fx-border-color: black");
			levelHBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println(value);
					if (LevelMenuController.selectedHBox != null)LevelMenuController.selectedHBox.setStyle("-fx-border-color: black");
					LevelMenuController.selectedHBox=levelHBox;
					LevelMenuController.selectedLevel=value;
					levelHBox.setStyle("-fx-border-color: black;-fx-background-color: #c4ffd5;");
				}
			});
			vboxLevels.getChildren().addAll(levelHBox);
		});

		ProfileManager p = new ProfileManager();
		profiles = p.getProfiles();
		profileNames = new ArrayList<String>();
		profiles.forEach(profile -> profileNames.add(profile.getName()));
		for (int i=1; i <= numberOfPlayers; i++) {
			ChoiceBox pChoiceBox = new ChoiceBox();
			pChoiceBox.getItems().addAll(profileNames);
			pChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
						System.out.println(pChoiceBox.getItems().get((newValue.intValue())));
					}
				}

			);
			vboxPlayes.getChildren().addAll(pChoiceBox);
		}

		System.out.println("Created LevelMenuController");
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

	private ArrayList<String> getLevels(){
		File actual = new File("./source/resources/levels");
		ArrayList<String> levels=new ArrayList<String>();
		for (File f : actual.listFiles()){
			levels.add(f.getName());
		}
		return levels;
	}
}
