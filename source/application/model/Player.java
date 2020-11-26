package application.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Player {

    private final StringProperty PlayerName;
    private final StringProperty Progress;
    private final StringProperty ProgressDescription;
    private final StringProperty password;
    private final StringProperty city;
    private final ObjectProperty<LocalDate> time;


    public Player() {
        this(null, null);
    }


    public Player(String PlayerName, String Progress) {
        this.PlayerName = new SimpleStringProperty(PlayerName);
        this.Progress = new SimpleStringProperty(Progress);


        this.ProgressDescription = new SimpleStringProperty("after the 3-2");
        this.password = new SimpleStringProperty("1234");
        this.city = new SimpleStringProperty("some city");
        this.time = new SimpleObjectProperty<LocalDate>(LocalDate.of(2021, 1, 1));
    }

    public String getPlayerName() {
        return PlayerName.get();
    }

    public void setPlayerName(String PlayerName) {
        this.PlayerName.set(PlayerName);
    }

    public StringProperty PlayerNameProp() {
        return PlayerName;
    }

    public String getProgress() {
        return Progress.get();
    }

    public void setProgress(String Progress) {
        this.Progress.set(Progress);
    }

    public StringProperty ProgressProp() {
        return Progress;
    }

    public String getProgressDescription() {
        return ProgressDescription.get();
    }

    public void setProgressDescription(String street) {
        this.ProgressDescription.set(street);
    }

    public StringProperty ProgressDescriptionProp() {
        return ProgressDescription;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProp() {
        return password;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProp() {
        return city;
    }

    public LocalDate getTime() {
        return time.get();
    }

    public void setTime(LocalDate birthday) {
        this.time.set(birthday);
    }

    public ObjectProperty<LocalDate> timeProp() {
        return time;
    }
}