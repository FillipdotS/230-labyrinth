package application;

import java.io.IOException;

import application.model.Player;
import application.view.PlayerOverviewControl;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameManagerUI extends Application {

    private Stage primaryStage;
    private BorderPane UI;
    private ObservableList<Player> playerData = FXCollections.observableArrayList();
    public GameManagerUI(){
    	playerData.add(new Player("Max", "3-2"));
    	playerData.add(new Player("Matthew", "3-2"));
    	playerData.add(new Player("Fillip", "4-5"));
    	playerData.add(new Player("lan", "4-2"));
    	playerData.add(new Player("Erik", "4-1"));
    	playerData.add(new Player("Liam", "3-9"));
    	playerData.add(new Player("Narcis", "4-0"));
    }
    
    public ObservableList<Player> getPlayerData() {
        return playerData;
    }
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Save Slot");

        initRootLayout();

        showPlayerOverview();
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GameManagerUI.class.getResource("view/UI.fxml"));
            UI = (BorderPane) loader.load();

            Scene scene = new Scene(UI);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showPlayerOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GameManagerUI.class.getResource("view/PlayerOverview.fxml"));
            AnchorPane playerOverview = (AnchorPane) loader.load();
            UI.setCenter(playerOverview);
            
            PlayerOverviewControl controller = loader.getController();
            controller.setGameManagerUI(this);
            
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
