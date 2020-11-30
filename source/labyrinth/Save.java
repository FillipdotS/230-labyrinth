package source.labyrinth;
/**
 * Save is used to get current Board status by Board method exposeself() and save it to a file, 
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Save {

	    private final StringProperty SaveID;
	    private final ObjectProperty<LocalDate> SaveTime;
	    private final StringProperty CurrentBoardState;//
	    private final StringProperty Description;


	    public Save() {
	    	this(null,null,null);
	    	
	    }
/**
 * 
 * @param SaveID 
 * @param CurrentBoardState get from Board.exposeself()
 * @param Description descrption added by player when saving
 */
	    public Save(String SaveID, String CurrentBoardState,String Description) {
	    	this.SaveID = new SimpleStringProperty(SaveID);
	        this.CurrentBoardState = new SimpleStringProperty(CurrentBoardState);
            this.SaveTime = new SimpleObjectProperty<LocalDate>(LocalDate.now());
            this.Description = new SimpleStringProperty(Description);
            
	    }

	    public String getSaveID() {
	        return SaveID.get();
	    }

	    public void setSaveID(String SaveID) {
	        this.SaveID.set(SaveID);
	    }

	    public StringProperty SaveIDProp() {
	        return SaveID;
	    }
	    
	    

	    public String getCurrentBoardState() {
	        return CurrentBoardState.get();
	    }

	    public void setCurrentBoardState(String CurrentBoardState) {
	        this.CurrentBoardState.set(CurrentBoardState);
	    }

	    public StringProperty CurrentBoardStateProp() {
	        return CurrentBoardState;
	    }


	    
	    public LocalDate getSaveTime() {
	        return SaveTime.get();
	    }

	    public void setSaveTime(LocalDate SaveTime) {
	    	
	        this.SaveTime.set(SaveTime);
	    }

	    public ObjectProperty<LocalDate> SaveTimeProp() {
	        return SaveTime;
	    }
	
	
	    public String getDescription() {
	        return Description.get();
	    }

	    public void setDescription(String Description) {
	        this.Description.set(Description);
	    }

	    public StringProperty DescriptionProp() {
	        return Description;
	    }
	
	
	
	    
	    
	    private static final String DATE_PATTERN = "H:m dd/MM/yyyy";

	    private static final DateTimeFormatter DATE_FORMATTER = 
	            DateTimeFormatter.ofPattern(DATE_PATTERN);


	    public static String format(LocalDate date) {
	        if (date == null) {
	            return null;
	        }
	        return DATE_FORMATTER.format(date);
	    }


	    
	    public static LocalDate parse(String dateString) {
	        try {
	            return DATE_FORMATTER.parse(dateString, LocalDate::from);
	        } catch (DateTimeParseException e) {
	            return null;
	        }
	    }


	    public static boolean validDate(String dateString) {
	        // Try to parse String.
	        return parse(dateString) != null;
	    }
	    
	    
	    
	    
	
	
}
