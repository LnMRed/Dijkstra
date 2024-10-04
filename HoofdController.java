package com.example.ov;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class HoofdController {
    ///////////////
    FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("TravelHome.fxml"));
    protected static final String USER_FILE = "users.json";
    protected static final String ROUTES_FILE = "routes.json";
    protected static ArrayList<User> users;
    protected static ArrayList<HashMap<String,String>> routes;
    protected static ArrayList<HashMap<String,String>> favoriteRoutes;
    static User currentUser;
    ///////////////

    @FXML
    protected String bassisStyle = "-fx-background-radius: 40px";
    @FXML
    protected String foutStyle = "-fx-background-color: rgba(255,119,112,0.7);-fx-background-radius: 40px";
    @FXML
    protected String goedGekeurdStyle = "-fx-background-color: rgba(99,232,176,0.69);-fx-background-radius: 40px";
    @FXML
    protected Label nietOvereen;
    @FXML
    protected  Label ongeldigInvoer;
    @FXML
    protected Label ongeldigVoornaam;
    @FXML
    protected Label ongeldigAchternaam;
    @FXML
    protected Label ongNieuWachtw;
    @FXML
    protected Label ongBevWachtw;
    @FXML
    protected Label ongEmail;
    @FXML
    protected Label ongWachtw;
    @FXML
    protected  Label verifiedResult;
    @FXML
    protected CheckBox wachtwoordBekijken;
    @FXML
    protected Image doneImg;
    @FXML
    protected TextField voornaam;
    @FXML
    protected TextField achternaam;
    @FXML
    protected TextField email;
    @FXML
    protected PasswordField nieuweWachtwoord;
    @FXML
    protected TextField zichtbaarWachtwoord;
    @FXML
    protected TextField bevestigZichtbaarWachtwoord;
    @FXML
    protected PasswordField bevestigWachtwoord;
    @FXML
    protected PasswordField huidigeWachtwoord;
    @FXML
    protected  Label doneLabel;
    @FXML
    protected VBox doneBox;
    protected final Mapping mapping = new Mapping();
    protected final Station station = new Station("station", LocalTime.of(9, 0) );

    /////////////////////


    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        HoofdController.currentUser = currentUser;
    }

    @FXML
    void backToHome() throws IOException {

    }

    @FXML
    void viewPassword(TextField viewedPassword, PasswordField password){
        if (wachtwoordBekijken.isSelected()) {
            password.setVisible(false);
            viewedPassword.setVisible(true);
            viewedPassword.setText(password.getText());
        }
        else if (!wachtwoordBekijken.isSelected()) {
            password.setVisible(true);
            viewedPassword.setVisible(false);
            password.setText(viewedPassword.getText());
        }

    }
    @FXML
    void directFeedback(){}
    @FXML
    void showLabelEvent(){}
    @FXML
    void resetLabel(){}
    @FXML
    void uitloggen() throws NullPointerException, IOException {}

}