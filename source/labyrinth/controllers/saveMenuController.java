package source.labyrinth.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import source.labyrinth.Save;
import source.labyrinth.SaveUI;
public class saveMenuController {
	    @FXML
	    private TableView<Save> saveTable;
	    @FXML
	    private TableColumn<Save, String> saveIDColumn;
	    @FXML
	    private TableColumn<Save, String> saveDescriptionColumn;

	    @FXML
	    private Label saveIDLabel;
	    @FXML
	    private Label saveTimeLabel;
	    @FXML
	    private Label saveDescriptionLabel;
	    
	    @FXML
	    private Label curBoardLabel;


	    private SaveUI SaveUI;


	    public saveMenuController() {
	    }


	    @FXML
	    private void initialize() {

	    	saveIDColumn.setCellValueFactory(cellData -> cellData.getValue().SaveIDProp());
	    	saveDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().DescriptionProp());
	    }


	    public void setSaveUI(SaveUI SaveUI) {
	        this.SaveUI = SaveUI;


	        saveTable.setItems(SaveUI.getSaveData());
	    }
	    
	    
	    private void showSaveDetails(Save save) {
	        if (save != null) {
	            // Fill the labels with info from the player object.
	        	
	        	saveIDLabel.setText(save.getSaveID());
	        	saveDescriptionLabel.setText(save.getDescription());
	        	curBoardLabel.setText(save.getCurrentBoardState());
	        	


	        } else {
	        	saveIDLabel.setText("");
	        	saveDescriptionLabel.setText("");
	        	curBoardLabel.setText("");
	        	saveTimeLabel.setText(Save.format(save.getSaveTime()));
	        }
	    }
	    
}
