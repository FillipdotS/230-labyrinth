package source.labyrinth;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import source.labyrinth.controllers.saveMenuController;
import source.labyrinth.Save;//will work if in the same package, will find a way fix it


public class SaveUI extends Application{
    private Stage primaryStage;
    private BorderPane SaveUI;
    private ObservableList<Save> SaveData = FXCollections.observableArrayList();
    private Board curBoard ;
    public SaveUI(){
    	//added some data test 
    	//TODO: get value from curBoard.exportSelf() and display it to save menu
    	SaveData.add(new Save("1", "curBoard.exportSelf()","save1"));
    	SaveData.add(new Save("2", "curBoard.exportSelf()","backup save"));
    	SaveData.add(new Save("3","curBoard.exportSelf()", "on 3,2"));
    	SaveData.add(new Save("4","curBoard.exportSelf()", "4-2"));
    	SaveData.add(new Save("5","curBoard.exportSelf(", "4-1"));
    	SaveData.add(new Save("6", "curBoard.exportSelf()","3-9"));
    	SaveData.add(new Save("7","curBoard.exportSelf()", "4-0"));
    }
    
    public ObservableList<Save> getSaveData() {
        return SaveData;
    }
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Save Slot");

        initRootLayout();

        showSaveOverview();
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SaveUI.class.getResource("source/resources/scenes/save_UI.fxml"));
            SaveUI = (BorderPane) loader.load();

            Scene scene = new Scene(SaveUI);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showSaveOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SaveUI.class.getResource("source/resources/scenes/save_menu.fxml"));
            AnchorPane saveMenu = (AnchorPane) loader.load();
            SaveUI.setCenter(saveMenu);
            
            saveMenuController controller = loader.getController();
            controller.setSaveUI(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
