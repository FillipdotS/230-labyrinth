package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Max
 * saveMenuController are able to load or delete saves
 */
public class saveMenuController implements Initializable {
    @FXML
    private VBox vboxSaves;
    @FXML
    private Button deleteSaveButton;
    @FXML
    private TextArea saveDetailTextArea;
    @FXML
    private Button loadSaveButton;
    private static String selectedSaveName;
    private static HBox selectedSaveHBox;


    /**
     * @param location -
     * @param resources -
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showSaveFile();
        System.out.println("Created SaveMenuController");
    }

    /**
     * method to display all saveData
     */
    private void showSaveFile() {
        vboxSaves.getChildren().clear();
        getSaves().forEach((savName) -> {
            HBox saveFile = new HBox(new Text(savName.substring(0, savName.length() - 4)));
            saveFile.setPrefHeight(30);
            saveFile.setAlignment(Pos.CENTER_LEFT);
            saveFile.setStyle("-fx-border-color: #c4fffd");
            deleteSaveButton.setDisable(true);
            loadSaveButton.setDisable(true);
            saveFile.setOnMouseClicked(event -> {
                deleteSaveButton.setDisable(false);
                loadSaveButton.setDisable(false);
                saveDetailTextArea.setText("SaveData name:" + "\n" + savName);
                System.out.println(savName);
                if (selectedSaveHBox != null) {
                    saveMenuController.getSelectedSaveHBox().setStyle("-fx-border-color: #c4fffd");

                }

                saveFile.setStyle("-fx-border-color: black;-fx-background-color: #ffb5b5;");
                setSelectedSaveHBox(saveFile);
                setSelectedSaveName(savName);

            });


            vboxSaves.getChildren().addAll(saveFile);
        });
    }

    /**
     * sub-method to get names from each of the save file
     *
     * @return savefile name
     */
    private ArrayList<String> getSaves() {
        File actual = new File("./source/resources/saves");
        ArrayList<String> saves = new ArrayList<>();
        for (File save : Objects.requireNonNull(actual.listFiles())) {
            saves.add(save.getName());
        }
        return saves;
    }

    /**
     * common method to back to main menu
     *
     * @param event click back button
     */
    @FXML
    public void returnToMainMenu(ActionEvent event) {
        System.out.println("Going back to main menu...");
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


    /**
     * method to delete save by using selectedSaveName
     */
    @FXML
    public void deleteSave() {
        if (selectedSaveHBox != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Save");
            alert.setHeaderText("Warning !");
            alert.setContentText("Are you sure you want to delete these files ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                File delFile = new File("./source/resources/saves/" + selectedSaveName);
                delFile.delete();

                Alert deleted = new Alert(Alert.AlertType.INFORMATION);
                deleted.setTitle("Delete Save");
                deleted.setHeaderText("File deleted");
                deleted.setContentText("File " + selectedSaveName + " deleted");
                deleted.showAndWait();

                // avoid to load a deleted file
                selectedSaveName = null;
                selectedSaveHBox = null;

                deleteSaveButton.setDisable(true);
                loadSaveButton.setDisable(true);
                showSaveFile();//refresh the saveData list
            }
            else {
                System.out.println("Delete Cancelled");
            }

        } else {
            System.out.println("Error Selection");
        }
    }

    /**
     * method to apply save and continue game
     *
     * @param event loadSave
     */
    @FXML
    public void loadSave(ActionEvent event) {
        if (selectedSaveName != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Load Save");
            alert.setHeaderText("Loading Save");
            alert.setContentText("Load this save and continue the game?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                LevelController.setNextSaveToLoad(selectedSaveName);
                try {
                    Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/level.fxml"));
                    Scene profileMenuScene = new Scene(profileMenuParent);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(profileMenuScene);
                    window.setTitle("Game");
                    window.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Load Cancelled");
            }



        }
    }


    /**
     * @return selectedSaveHBox - HBox with saveName
     */
    public static HBox getSelectedSaveHBox() {
        return selectedSaveHBox;
    }

    /**
     * @param selectedSaveHBox - a selected HBox
     */
    public static void setSelectedSaveHBox(HBox selectedSaveHBox) {
        saveMenuController.selectedSaveHBox = selectedSaveHBox;
    }

    /**
     * @param selectedSaveName name of each save
     */
    public static void setSelectedSaveName(String selectedSaveName) {
        saveMenuController.selectedSaveName = selectedSaveName;
    }


}
