package source.labyrinth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import source.labyrinth.controllers.LevelMenuController;

/**
 * @author Max
 * OLD SAVE MANAGER, please user the new one(saveMenuController)
 * a save manager UI including function, no fxml required
 * actually crash about fxml fx:id so code FX here may easier to me, sorry about that
 */
public class SaveManager extends Application implements EventHandler<ActionEvent> {

    private Button btBrowseSave;
    private Button btLoad;
    private Button btCancel;
    private Button btDelete;
    private Button buttonReadLine;
    private Label labelFileName;
    private Text textLineFields;
    private ChoiceBox choiceBoxDelimiter;
    private BufferedReader reader = null;
    private int lineCount = 0;
    TableView<Save> table = new TableView<Save>();
    private ObservableList<Save> data = FXCollections.observableArrayList();//pre-set of testing value
    private ObservableList<Save> data2 = FXCollections.observableArrayList();//txt from saveFolder


    @Override

    public void start(Stage stage) {
        // creating a table title and buttons
        Label saveTableTitle = new Label("Save:");
        Font font = Font.font("Ariel", FontWeight.BOLD, FontPosture.REGULAR, 16);
        saveTableTitle.setFont(font);

        btBrowseSave = new Button("Browse save files");
        btBrowseSave.setOnAction(this);
        btBrowseSave.setMaxWidth(150);

        btLoad = new Button("Load Save");
        btLoad.setOnAction(this);
        btLoad.setMaxWidth(150);

        btCancel = new Button("Cancel");
        btCancel.setOnAction(this);
        btCancel.setMaxWidth(150);

        btDelete = new Button("Delete Save");
        btDelete.setOnAction(this);
        btDelete.setMaxWidth(150);

        // put all buttons into buttonBox
        VBox buttonBox = new VBox();
        buttonBox.setPadding(new Insets(10, 10, 10, 10)); // Sets the space around the buttonBox.
        buttonBox.setSpacing(10); // Sets the vertical space in pixels between buttons within the box.

        buttonBox.getChildren().addAll(btBrowseSave, btLoad, btCancel, btDelete

        );


        // Creating a table view pre-set of testing value
        data = FXCollections.observableArrayList(

                new Save("file1", "descript", "2-3", "12/01/2017"),
                new Save("file2", "descript", "3-5", "01/11/2019"),
                new Save("file3", "descript", "5-4", "12/04/2017"),
                new Save("file4", "descript", "7-2", "25/09/2018"));


        //Try coping some code from LevelReader to loop and print txtname and it works
        File actual = new File("./source/resources/saves");

        for (File f : actual.listFiles()) {
            data2 = FXCollections.observableArrayList(
                    new Save(f.getName(), "descript", "4-2", "12/01/2017"));
        }


        // Creating columns
        TableColumn txtNameCol = new TableColumn("SaveData");
        txtNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        TableColumn DescriptCol = new TableColumn("Description");
        DescriptCol.setCellValueFactory(new PropertyValueFactory("descript"));
        TableColumn tilesCol = new TableColumn("Tiles");
        tilesCol.setCellValueFactory(new PropertyValueFactory("Tiles"));
        TableColumn dateCol = new TableColumn("Last ");
        dateCol.setCellValueFactory(new PropertyValueFactory("dateModified"));
        dateCol.setPrefWidth(100);

        // Adding data to the table
        ObservableList<Save> list = FXCollections.observableArrayList();
        table.setItems(data2);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(txtNameCol, DescriptCol, tilesCol, dateCol);
        // set table property
        table.setMaxSize(400, 300);


        // put table to vbox2
        VBox tableBox = new VBox();
        tableBox.setSpacing(5);
        tableBox.setPadding(new Insets(10, 30, 30, 30));
        tableBox.getChildren().addAll(saveTableTitle, table);


        // put all things together, left with tableBox and right with buttonBox
        SplitPane sp = new SplitPane();
        final StackPane sp1 = new StackPane();
        sp1.getChildren().add(tableBox);
        final StackPane sp2 = new StackPane();
        sp2.getChildren().add(buttonBox);

        sp.getItems().addAll(sp1, sp2);
        sp.setDividerPositions(0.7f, 1.0f);

        // Setting the scene

        Scene scene = new Scene(sp, 600, 400);

        stage.setTitle("SaveData");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * setting of methods for buttons
     *
     * @param browseFile
     */
    @Override
    public void handle(ActionEvent browseFile) {
        Object source = browseFile.getSource();

        if (source == btBrowseSave)
            openFile();
        if (source == btLoad)
            loadSelectedSave();
        if (source == btCancel)
            cancel();
        if (source == btDelete)
            ;

    }


    private boolean openFile() {
        FileChooser saveBrowser = new FileChooser();
        saveBrowser.setTitle("Open Text File");
        saveBrowser.setInitialDirectory(new File("."));
        saveBrowser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
        File selectedSave = saveBrowser.showOpenDialog(null);
        if (selectedSave == null) {
            String saveName = "Fox.txt";
            try {
                reader = new BufferedReader(new FileReader("data/" + saveName));
            } catch (IOException e) {
                return false;
            }
            labelFileName.setText(saveName);
            buttonReadLine.setDisable(false);
        } else {
            try {
                reader = new BufferedReader(new FileReader(selectedSave));
                buttonReadLine.setDisable(false);
            } catch (IOException e) {/* showErrorDialog("IO Exception: " + e.getMessage()); */
                return false;
            }
            labelFileName.setText(selectedSave.getName());
        }

        textLineFields.setText("");
        lineCount = 0;
        return true;
    }

    private void loadSelectedSave() {

        System.out.println("yeet");

    }

    private void deleteSelectedSave() {
        table.getItems().removeAll(table.getSelectionModel().getSelectedItem());

    }


    private void cancel() {

    }


}
