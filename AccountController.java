package com.example.ov;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class AccountController extends HoofdController {


    ///////////////////////
    public String getFirstname() {
        return voornaam.getText();
    }

    public String getLastname() {
        return achternaam.getText();
    }

    public String getEmail() {
        return email.getText();
    }

    public String getNewPassword() {
        return nieuweWachtwoord.getText();
    }

    public String getConfirmNewPassword() {
        return bevestigWachtwoord.getText();
    }

    //////////////////////
    //wisselen tussen textField en passwordField value,
    //om het wachtwoord te bekijken,
    //en dan set het textField text als huidige wachtwoord.
    @FXML
    void viewPasswordClicked() {
        viewPassword(zichtbaarWachtwoord, nieuweWachtwoord);
        viewPassword(bevestigZichtbaarWachtwoord, bevestigWachtwoord);
    }
    //controleer alle velden dan als alles in orde maak nieuwe User object en voeg hem in users ArrayList toe,
    //daarna bewaart die nieuwe ArrayList via saveUser().
    @FXML
    void addUser() throws IOException {
        try {
            System.out.println("voornaam: " + voornaam.getText());
            System.out.println("achternaam: " + achternaam.getText());
            System.out.println("email: " + email.getText());
            System.out.println("wachtwoord: " + nieuweWachtwoord.getText());
            System.out.println("nieuweWachtwoord: " + getNewPassword());
            System.out.println("bevestigWachtwoord: " +getConfirmNewPassword());

            if ((!voornaam.getText().isEmpty() && !achternaam.getText().isEmpty() && !email.getText().isEmpty() && !nieuweWachtwoord.getText().isEmpty())
                    && getNewPassword().equals(getConfirmNewPassword())) {
                User user = new User(getFirstname(), getLastname(), getEmail(), getNewPassword());
                User.addUser(user);
                System.out.println("user toevoegt");
                doneBox.setVisible(true);
                System.out.println("doneBox laten zien");
                backToHome();
                System.out.println("backToHome roept");

            }else {
                if (getFirstname().isEmpty()) {
                    ongeldigVoornaam.setText("* Oops! dit is een verplicht veld ");
                    voornaam.setStyle(foutStyle);
                    voornaam.requestFocus();
                }
                if (getLastname().isEmpty()) {
                    ongeldigAchternaam.setText("* Oops! dit is een verplicht veld ");
                    achternaam.setStyle(foutStyle);
                    achternaam.requestFocus();
                }
                if (getEmail().isEmpty()) {
                    ongEmail.setText("* Oops! dit is een verplicht veld ");
                    email.setStyle(foutStyle);
                    email.requestFocus();
                }
                if (getNewPassword().isEmpty()||zichtbaarWachtwoord.getText().isEmpty()) {
                    ongWachtw.setText("* Oops! dit is een verplicht veld ");
                    nieuweWachtwoord.setStyle(foutStyle);
                    zichtbaarWachtwoord.setStyle(foutStyle);
                    nieuweWachtwoord.requestFocus();
                }
                if (getConfirmNewPassword().isEmpty()||bevestigWachtwoord.getText().isEmpty()) {
                    nietOvereen.setText("* Oops! dit is een verplicht veld ");
                    bevestigWachtwoord.setStyle(foutStyle);
                    bevestigZichtbaarWachtwoord.setStyle(foutStyle);
                } else if (!getConfirmNewPassword().equals(getNewPassword())) {
                    nietOvereen.setText("* Oops! geen wachtwoormatch");
                    huidigeWachtwoord.setStyle(foutStyle);
                    zichtbaarWachtwoord.setStyle(foutStyle);
                    bevestigWachtwoord.setStyle(foutStyle);
                    bevestigZichtbaarWachtwoord.setStyle(foutStyle);
                    nieuweWachtwoord.requestFocus();
                }
            }


        } catch (NullPointerException e) {
            e.getMessage();
            ongeldigInvoer.setText("Oops! de velden zijn leeg!");
        }
    }
    @Override
    @FXML
    void directFeedback(){
        if (getNewPassword().equals(getConfirmNewPassword())) {
            nieuweWachtwoord.setStyle(goedGekeurdStyle);
            zichtbaarWachtwoord.setStyle(goedGekeurdStyle);
            bevestigWachtwoord.setStyle(goedGekeurdStyle);
            bevestigZichtbaarWachtwoord.setStyle(goedGekeurdStyle);
        }else {
            nietOvereen.setText("* Wachtwoorden komen niet overeen");
            nieuweWachtwoord.setStyle(foutStyle);
            zichtbaarWachtwoord.setStyle(foutStyle);
            bevestigWachtwoord.setStyle(foutStyle);
            bevestigZichtbaarWachtwoord.setStyle(foutStyle);
        }
    }
    //reset alle fouts mededelen labels naar “null” als de gebruiker begint te typen.
    @Override
    void resetLabel() {
        ongeldigVoornaam.setText("");
        voornaam.setStyle(bassisStyle);

        ongeldigAchternaam.setText("");
        achternaam.setStyle(bassisStyle);

        ongEmail.setText("");
        email.setStyle(bassisStyle);

        ongWachtw.setText("");
        nieuweWachtwoord.setStyle(bassisStyle);
        zichtbaarWachtwoord.setStyle(bassisStyle);

        ongeldigInvoer.setText("");
        nietOvereen.setText("");
        bevestigWachtwoord.setStyle(bassisStyle);
        bevestigZichtbaarWachtwoord.setStyle(bassisStyle);


    }
    //in het geval de gebruiker wil niet meer account maken,
    //brengt het programma hem 	naar het Travel inlog venster weer.
    @Override
    void backToHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) bevestigWachtwoord.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
