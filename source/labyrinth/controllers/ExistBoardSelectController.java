package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
 *
 */
public class ExistBoardSelectController implements Initializable {
    private static String selectedLevel;
    private static HBox selectedHBox;


    @FXML private VBox vboxLevels;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        renderLevels();
        System.out.println("Created LevelMenuController");
    }

    /**
     * from level menu
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
                if (selectedHBox != null) {
                    selectedHBox.setStyle("-fx-border-color: black");
                }
                selectedHBox = levelHBox;
                selectedLevel = value.substring(0,value.length()-4);
                System.out.println(selectedLevel);
                levelHBox.setStyle("-fx-border-color: black;-fx-background-color: #c4ffd5;");
                //renderLeaderBoard();
            });
            vboxLevels.getChildren().addAll(levelHBox);
        });
    }

    /**
     *    from level menu
     */

    private ArrayList<String> getLevels() {
        File levelsFiles = new File("./source/resources/levels");
        ArrayList<String> levels = new ArrayList<>();
        for (File f : Objects.requireNonNull(levelsFiles.listFiles())) {
            levels.add(f.getName());
        }
        return levels;
    }



    @FXML
    public void goToBoardEditor(ActionEvent event) {
        System.out.println("Board Editor");
        try {
            Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level_editor.fxml"));
            Scene profileMenuScene = new Scene(profileMenuParent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setScene(profileMenuScene);
            window.setTitle("Board Editor");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void returnToLevelEditorMenu(ActionEvent event) {
        System.out.println("Editor Menu");
        try {
            Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/editor_menu.fxml"));
            Scene profileMenuScene = new Scene(profileMenuParent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setScene(profileMenuScene);
            window.setTitle("Editor Menu");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
