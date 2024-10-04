package com.example.ov;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class LoginController extends HoofdController {

    /////////////////////////////////////////

    public void setHuidigeWachtwoord(String password) {
        this.huidigeWachtwoord.setText(password);
    }

    public String getHuidigeGebruiker() {
        return email.getText();
    }

    public String getHuidigeWachtwoord() {
        return huidigeWachtwoord.getText();
    }

    public String getZichtbaarHuidigeWachtwoord() {
        return zichtbaarWachtwoord.getText();
    }
    ///////////////////////////////////////

    //wisselen tussen textField en passwordField value,
    //om het wachtwoord te bekijken en dan set het textField text als currentPassword.
    @FXML
    void viewPasswordClicked() {
        viewPassword(zichtbaarWachtwoord, huidigeWachtwoord);
        setHuidigeWachtwoord(zichtbaarWachtwoord.getText());
    }

    //In het geval dat de gebruiker wil zijn account  maken,
    //brengt het programma hem naar het accountMaken venster wanneer hij op de link”heb je nog geen account?” drukt.
    @FXML
    void addUserLinkClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("account-maken.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) email.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    //In het geval dat de gebruiker wil zijn wachtwoord veranderen,
    //brengt het programma hem naar het reset venster wanneer hij op de link”Wachtwoord wijzigen?” drukt.
    @FXML
    void resetPasswordLinkClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("reset.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) email.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    //user account verification
    //get email en wachtwoord en zoek voor match een de users file,
    //in het geval dat gevonden laat de user zijn wachtwoord wijzigen en bewaar de nieuwe wachtwoord.
    public boolean verificationService(String email, String password) {
        System.out.print("verificationService triggered");
        try (Reader reader = new FileReader(USER_FILE)) {
            if (reader.ready()) {
                // JSON file reading
                JsonArray users = JsonParser.parseReader(reader).getAsJsonArray();
                System.out.println("jsonArray gemaakt");
                // verification
                for (int i = 0; i < users.size(); i++) {
                    System.out.println(i + " for loop");
                    JsonObject user = users.get(i).getAsJsonObject();
                    System.out.println(user + " : bestaat wel");
                    String storedUsername = user.get("E-mail").getAsString();
                    String storedPassword = user.get("Wachtwoord").getAsString();
                    if (storedUsername.equals(email) && storedPassword.equals(password)) {
                        User.setCurrentUser(user.get("Voornaam").getAsString(), user.get("Achternaam").getAsString(), user.get("E-mail").getAsString(), user.get("Wachtwoord").getAsString());
                        System.out.println("gonna to make java user ");
                        currentUser = User.getCurrentUser();
                        System.out.println("Login geverifieerd");
                        return true;
                    }
                }
                System.out.println("Login niet geverifieerd");
                return false;
            } else verifiedResult.setText("er is nog geen gebruiker aangemeld");
        } catch (IOException NullPointerException) {
            System.out.println("oops");
            verifiedResult.setText("Ongeldige gegevens");
            throw new RuntimeException();
        }
        return false;
    }

    //reset alle fout mededelen labels naar “null” als de gebruiker begint te typen.
    @Override
    public void resetLabel() {
        try {
            ongEmail.setText("");
            email.setStyle(bassisStyle);
            ongWachtw.setText("");
            huidigeWachtwoord.setStyle(bassisStyle);
            ongeldigInvoer.setText("");
        } catch (NullPointerException NullPointerException) {
            NullPointerException.getMessage();
        }
    }

    //go to the main window
    //in het geval ge gebruiker gegevens klopt wel brengt het programma hem naar het TravelHome venster.
    @FXML
    void loginOnAction() throws NullPointerException, IOException {
        try(Reader reader = new FileReader(USER_FILE)){
            if (getHuidigeGebruiker().isEmpty()) {
                ongEmail.setText("* geen e-mailadres ingevuld");
                email.setStyle(foutStyle);
                email.requestFocus();
            }
            if (!getHuidigeGebruiker().isEmpty()) {
                // JSON file reading
                JsonArray users = JsonParser.parseReader(reader).getAsJsonArray();
                // verification
                for (int i = 0; i < users.size(); i++) {
                    JsonObject user = users.get(i).getAsJsonObject();
                    String storedUsername = user.get("E-mail").getAsString();
                    if (storedUsername.equals(getHuidigeGebruiker())) {
                        System.out.println("Gebruiker naam bestaat wel");
                        email.setStyle(goedGekeurdStyle);
                        huidigeWachtwoord.requestFocus();
                    }
                }
            } if(getHuidigeWachtwoord().isEmpty()) {
                ongWachtw.setText("* Geen Wachtwoord");
                huidigeWachtwoord.setStyle(foutStyle);

            }else if (verificationService(getHuidigeGebruiker(),getHuidigeWachtwoord())){
                FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("TravelHome.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
                Stage stage = (Stage) verifiedResult.getScene().getWindow();
                stage.setScene(scene);
                stage.setScene(scene);
                stage.show();

            }else{
                ongEmail.setText("* Account is ons niet bekend");
                email.setStyle(foutStyle);
                huidigeWachtwoord.setStyle(foutStyle);
                System.err.println("Login not verified");
            }
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
            /*if (!getCurrentUsername().isEmpty() && !getCurrentPassword().isEmpty() && verificationService(getCurrentUsername(), getCurrentPassword())) {

            }if (getCurrentUsername().isEmpty()) {
                invalidEmail.setText("* Gebruiker naam is verplicht");
                email.setStyle(foutStyle);
                email.requestFocus();
            }if (getCurrentPassword().isEmpty()) {
                invalidPassword.setText("* Geen wachtwoord");
                currentPassword.setStyle(foutStyle);
                currentPassword.requestFocus();
            } else{
                verifiedResult.setText("* Oops! \nOngeldig inlog gegevens");
                email.setStyle(foutStyle);
                currentPassword.setStyle(foutStyle);
            }

        } catch (NullPointerException e) {
            e.getMessage();
        }*/
    }

    //in het geval de gebruiker wil niet meer inloggen,
    //brengt het programma hem naar het TravelHome venster weer zonder current user gegevens.
    @Override
    void backToHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("TravelHome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) huidigeWachtwoord.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
