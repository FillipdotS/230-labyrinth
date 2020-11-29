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
	@FXML private Button addPlayerButton;
	@FXML private Button removePlayerButton;


	private static String selectedLevel;
	private static HBox selectedHBox;
	private static int numberOfPlayers =  2;
	private static ArrayList<Object> profilesChosen = new ArrayList<Object>();
	private static ArrayList<Profile> profiles;
	private static ArrayList<String> profileNames;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		renderLevels();
		renderPlayersChoiceBox();

		addPlayerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				numberOfPlayers = numberOfPlayers==4?4:numberOfPlayers+1;
				renderPlayersChoiceBox();
			}
		});
		removePlayerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				numberOfPlayers = numberOfPlayers==2?2:numberOfPlayers-1;
				if(profilesChosen.size() > numberOfPlayers) profilesChosen.remove(numberOfPlayers);
				renderPlayersChoiceBox();
			}
		});

		System.out.println("Created LevelMenuController");
	}

	private  void renderLevels(){
		vboxLevels.getChildren().clear();
		getLevels().forEach((value) -> {
			HBox levelHBox = new HBox(new Text(value.substring(0,value.length()-4)));
			levelHBox.setPrefHeight(30);
			levelHBox.setAlignment(Pos.CENTER_LEFT);
			levelHBox.setStyle("-fx-border-color: black");
			levelHBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println(value);
					if (selectedHBox != null)LevelMenuController.getSelectedHBox().setStyle("-fx-border-color: black");
					setSelectedHBox(levelHBox);
					setSelectedLevel(value);
					levelHBox.setStyle("-fx-border-color: black;-fx-background-color: #c4ffd5;");
				}
			});
			vboxLevels.getChildren().addAll(levelHBox);
		});
	}

	private void renderPlayersChoiceBox(){
		vboxPlayes.getChildren().clear();
		ProfileManager p = new ProfileManager();
		profiles = p.getProfiles();
		profileNames = new ArrayList<String>();
		profiles.forEach(profile -> profileNames.add(profile.getName()));
		for (int i=0; i < numberOfPlayers; i++) {
			ChoiceBox pChoiceBox = new ChoiceBox();
			pChoiceBox.setPrefWidth(250);
			pChoiceBox.getItems().addAll(profileNames);

			profilesChosen.forEach(prof -> pChoiceBox.getItems().remove(prof));
			if (profilesChosen.size() > i)pChoiceBox.getItems().addAll(profilesChosen.get(i));
			if (profilesChosen.size() > i) pChoiceBox.getSelectionModel().select(profilesChosen.get(i));

			pChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
						Object obj = pChoiceBox.getItems().get((newValue.intValue()));
						System.out.println(obj.toString());
						System.out.println(oldValue);
						if (oldValue!=(Integer)(-1)) profilesChosen.remove(pChoiceBox.getItems().get((oldValue.intValue())));
						profilesChosen.add(pChoiceBox.getItems().get((newValue.intValue())));
						renderPlayersChoiceBox();
					}
				}
			);
			vboxPlayes.getChildren().addAll(pChoiceBox);
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

	private ArrayList<String> getLevels(){
		File actual = new File("./source/resources/levels");
		ArrayList<String> levels=new ArrayList<String>();
		for (File f : actual.listFiles()){
			levels.add(f.getName());
		}
		return levels;
	}

	public static HBox getSelectedHBox() {return selectedHBox;}

	public static void setSelectedHBox(HBox selectedHBox) {LevelMenuController.selectedHBox = selectedHBox;}

	public static String getSelectedLevel() {return selectedLevel;}

	public static void setSelectedLevel(String selectedLevel) {LevelMenuController.selectedLevel = selectedLevel;}

	public static int getNumberOfPlayers() {return numberOfPlayers;}

	public static void setNumberOfPlayers(int numberOfPlayers) {LevelMenuController.numberOfPlayers = numberOfPlayers;}

	public static ArrayList<Profile> getProfiles() {return profiles;}

	public static void setProfiles(ArrayList<Profile> profiles) {LevelMenuController.profiles = profiles;}

	public static ArrayList<String> getProfileNames() {return profileNames;}

	public static void setProfileNames(ArrayList<String> profileNames) {LevelMenuController.profileNames = profileNames;}
}
