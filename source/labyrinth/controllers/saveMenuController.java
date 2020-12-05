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
 * @author Max(acutally is Fillip)
 * This class heavily copied from LevelMenuController and some functions even the same, apologise for I am such an idiot that don't know how to start
 * the load save and play function is not changed yet(still = loadGame in LevelMenuController)
 * added delete file function
 * working on load save function
 * want add a TextArea to show all details inside save.ser but don't know possible or not
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



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showSaveFile();

        System.out.println("Created SaveMenuController");
    }

    /**
     * I am sorry that I have to copy Fillip code to make it works
     */
    private void showSaveFile() {
        vboxSaves.getChildren().clear();
        getSaves().forEach((value) -> {
            HBox saveFile = new HBox(new Text(value.substring(0, value.length() - 4)));
            saveFile.setPrefHeight(30);
            saveFile.setAlignment(Pos.CENTER_LEFT);
            saveFile.setStyle("-fx-border-color: #c4fffd");
            deleteSaveButton.setDisable(true);
            loadSaveButton.setDisable(true);
            saveFile.setOnMouseClicked(event -> {
                deleteSaveButton.setDisable(false);
                loadSaveButton.setDisable(false);
                System.out.println(value);
                if (selectedSaveHBox != null) {
                    saveMenuController.getSelectedSaveHBox().setStyle("-fx-border-color: #c4fffd");

                }

                saveFile.setStyle("-fx-border-color: black;-fx-background-color: #ffb5b5;");
                setSelectedSaveHBox(saveFile);
                setSelectedSaveName(value);

            });


            vboxSaves.getChildren().addAll(saveFile);
        });
    }


    @FXML
    public void returnToMainMenu(ActionEvent event) {
        System.out.println("Going back to main menu...");
        try {
            Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
            Scene profileMenuScene = new Scene(profileMenuParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(profileMenuScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getSaves() {
        File actual = new File("./source/resources/saves");
        ArrayList<String> saves = new ArrayList<>();
        for (File f : Objects.requireNonNull(actual.listFiles())) {
            saves.add(f.getName());
        }
        return saves;
    }

    /**
     * method to delete save
     */
    @FXML
    public void deleteSave() {
        if (selectedSaveHBox != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Warning !");
            alert.setContentText("Are you sure you want to delete these files ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                File delFile = new File("./source/resources/saves/" + selectedSaveName);
                delFile.delete();
                Alert deleted = new Alert(Alert.AlertType.INFORMATION);
                deleted.setTitle("Delete Save");
                deleted.setHeaderText("File deleted");
                deleted.setContentText("File "+selectedSaveName+" deleted");
                deleted.showAndWait();
                deleteSaveButton.setDisable(true);
                loadSaveButton.setDisable(true);
                showSaveFile();
            }

        } else {
            System.out.println("Error Selection");
        }
    }

   /* @FXML
    public void loadGame(ActionEvent event) {
        if (selectedSaveName != null) {
            System.out.println("loading save ...");
            profilesChosen.forEach(obj -> System.out.println(obj.toString()));
            String[] prof = new String[numberOfPlayers];
            for (int i = 0; i < prof.length; i++) {
                if (profilesChosen.size() > i) prof[i] = profilesChosen.get(i).toString();
            }
            saveMenuController.setNextLevelProfiles(prof);
            saveMenuController.setNextLevelToLoad(selectedSaveName);

            System.out.println("level: " + selectedSaveName);
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
            alert.setContentText("Choose a save!");
            alert.showAndWait();
        }
    }*/


    public static HBox getSelectedSaveHBox() {
        return selectedSaveHBox;
    }

    public static void setSelectedSaveHBox(HBox selectedSaveHBox) {
        saveMenuController.selectedSaveHBox = selectedSaveHBox;
    }

/*    public static String getSelectedSaveName() {
        return selectedSaveName;
    }*/

    public static void setSelectedSaveName(String selectedSaveName) {
        saveMenuController.selectedSaveName = selectedSaveName;
    }


}