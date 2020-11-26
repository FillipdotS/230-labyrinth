package application.view;

import application.GameManagerUI;
import application.model.Player;
import application.util.DateConvert;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class PlayerOverviewControl {
	  @FXML
	    private TableView<Player> playerTable;
	    @FXML
	    private TableColumn<Player, String> PlayerNameColumn;
	    @FXML
	    private TableColumn<Player, String> ProgressColumn;

	    @FXML
	    private Label PlayerNameLabel;
	    @FXML
	    private Label progressLabel;
	    @FXML
	    private Label ProgressDescriptionLabel;
	    @FXML
	    private Label passwordCodeLabel;
	    @FXML
	    private Label cityLabel;
	    @FXML
	    private Label timeLabel;


	    private GameManagerUI GameManagerUI;


	    public PlayerOverviewControl() {
	    }


	    @FXML
	    private void initialize() {

	    	PlayerNameColumn.setCellValueFactory(cellData -> cellData.getValue().PlayerNameProp());
	    	ProgressColumn.setCellValueFactory(cellData -> cellData.getValue().ProgressProp());
	    }


	    public void setGameManagerUI(GameManagerUI GameManagerUI) {
	        this.GameManagerUI = GameManagerUI;


	        playerTable.setItems(GameManagerUI.getPlayerData());
	    }
	    
	    
	    private void showPlayerDetails(Player player) {
	        if (player != null) {
	            // Fill the labels with info from the player object.
	        	PlayerNameLabel.setText(player.getPlayerName());
	        	progressLabel.setText(player.getProgress());
	        	ProgressDescriptionLabel.setText(player.getProgressDescription());
	        	passwordCodeLabel.setText(player.getPassword());
	            cityLabel.setText(player.getCity());

	        } else {
	        	PlayerNameLabel.setText("");
	        	progressLabel.setText("");
	        	ProgressDescriptionLabel.setText("");
	        	passwordCodeLabel.setText("");
	            cityLabel.setText("");
	            timeLabel.setText(DateConvert.format(player.getTime()));
	        }
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
}
