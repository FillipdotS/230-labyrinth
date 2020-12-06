package source.labyrinth.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import source.labyrinth.Profile;
import source.labyrinth.ProfileManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class LeaderboardController implements Initializable {

    @FXML private TableView<Profile> tableview;
    @FXML private TableColumn<Profile,Integer> id;
    @FXML private TableColumn<Profile,String> name;
    @FXML private TableColumn<Profile,Integer> total;
    @FXML private TableColumn<Profile,String> wins;
    @FXML private TableColumn<Profile,String> loses;
    private  ProfileManager pm = new ProfileManager();
    private ArrayList<Profile> leaderboards = new ArrayList<Profile>();
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Created LeaderboardController");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        total.setCellValueFactory(new PropertyValueFactory<>("totalPlayed"));
        wins.setCellValueFactory(new PropertyValueFactory<>("wins"));
        loses.setCellValueFactory(new PropertyValueFactory<>("losses"));
        id.setCellValueFactory(new PropertyValueFactory<>("ID"));
        leaderboard(pm.getProfiles());
        tableview.getItems().setAll(leaderboards);

	}

    public  void leaderboard(ArrayList<Profile> x){
        Profile[] appending = new Profile[x.size()];
        for(int i = 0; i < x.size() ; i++){
            appending = addX(appending,x.get(i));
        }
        for(int i = 0; i < appending.length-(x.size()-1);i++){
            leaderboards.add(new Profile(appending[i].getName(),appending[i].getID(),appending[i].getTotalPlayed(),appending[i].getWins(),appending[i].getLosses()));
            getPlayerInfo(appending[i]);
        }
    }
    /*
    * sorting
    * */
    public static Profile[] addX(Profile[] array,Profile add){
        Profile[] dest_Array = new Profile[array.length+1];
        int val = add.getWins();
        if(array[0]==null){
            array[0]= add;
            return array;
        }
        int index = 0;
        int j = 0;
        boolean checker= false;
        if(array[index].getWins() < val){
            checker=true;
        }
        while(!checker){
            if (array[index].getWins() > val){
                checker=true;
            }else if (array[index].getWins() == val){
                if(array[index].getLosses()<add.getLosses()){
                    index++;
                    checker = true;
                }else checker = true;
            }
            else index++;
        }
        for(int i = 0; i < dest_Array.length; i++) {
            if(i == index) {
                dest_Array[i] = add;
            }
            else {
                dest_Array[i] = array[j];
                j++;
            }
        }
        return dest_Array;
    }
    /*
    * printing data
    * */
    public static void getPlayerInfo(Profile prof){
        String username = prof.getName();
        Integer userid = prof.getID();
        Integer totalPlayed = prof.getTotalPlayed();
        Integer wins = prof.getWins();
        Integer loses = prof.getLosses();
        System.out.println("Player: "+username+"\n" +"ID: "+userid+ "\n"+ "TotalPlay Count: "+totalPlayed+ "\n"+ "Win/Lose: "+wins+"/"+loses);
    }
    @FXML
    public void returnToMainMenu(ActionEvent event) {
        System.out.println("Going back to main menu...");
        try {
            Parent profileMenuParent = FXMLLoader.load(getClass().getResource("../../resources/scenes/main_menu.fxml"));
            Scene profileMenuScene = new Scene(profileMenuParent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setScene(profileMenuScene);
            window.setTitle("Main Menu");
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
