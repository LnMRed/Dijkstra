package com.example.ov;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static com.example.ov.User.*;


public class
ResetController extends HoofdController {
    User currentUser;

    @FXML
    protected PasswordField oudeWachtwoord;

    @FXML
    protected Label herstelKnop;
    @FXML
    protected ImageView herstelKnopIcon;


    ////////////////////////////////////
    public String getHuidigeGebruiker() {return email.getText();}
    public String getOudeWachtwoord() {return oudeWachtwoord.getText();}
    public String getNieuweWachtwoord() {return nieuweWachtwoord.getText();}
    public String getConfirmNewPassword() {return bevestigWachtwoord.getText();}

    ////////////////////////////////////
    //bring current user details from Json file
    //Deze methode haalt de e-mail en het oude wachtwoord op uit het FXML-bestand,
    //en zoekt naar een overeenkomst in het JSON-bestand "users" via een for-loop.
    //Als er een overeenkomst wordt gevonden,
    //stopt de methode en retourneert true; anders retourneert ze false.
    public boolean verificationResetService(String email, String oldPassword) {
        try (Reader reader = new FileReader(USER_FILE)) {
            // JSON file reading
            JsonArray users = JsonParser.parseReader(reader).getAsJsonArray();
            // verification
            for (int i = 0; i < users.size(); i++) {
                JsonObject user = users.get(i).getAsJsonObject();
                String storedUsername = user.get("E-mail").getAsString();
                String storedPassword = user.get("Wachtwoord").getAsString();
                if (storedUsername.equals(email) && storedPassword.equals(oldPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return false;
    }

    @FXML
    void resetAccount() throws IOException {
        //Als je op de resetknop drukt.
        if(getNieuweWachtwoord().isEmpty()) {
            ongNieuWachtw.setText("* geen wachtwoord ingevuld");
            nieuweWachtwoord.setStyle(foutStyle);
            nieuweWachtwoord.requestFocus();
        }
        if(getConfirmNewPassword().isEmpty()) {
            ongBevWachtw.setText("* geen wachtwoord ingevuld");
            bevestigWachtwoord.setStyle(foutStyle);
            bevestigWachtwoord.requestFocus();
        }else if (getNieuweWachtwoord().equals(getConfirmNewPassword())) {
            //roept deze methode de resetmethode aan om het wachtwoord te wijzigen
            nieuweWachtwoord.setStyle(goedGekeurdStyle);
            bevestigWachtwoord.setStyle(goedGekeurdStyle);
            resetPassword(getHuidigeGebruiker(), getNieuweWachtwoord());
            doneBox.setVisible(true);
            //en keert daarna terug naar het inlogvenster.
        }else {
            nieuweWachtwoord.setStyle(foutStyle);
            bevestigWachtwoord.setStyle(foutStyle);
            nietOvereen.setText("Wachtwoorden komen niet overeen");
        }

    }
    //wisselen tussen textField en passwordField value,
    //om het wachtwoord te bekijken
    //en dan set het textField text als huidige wachtwoord.
    @FXML
    void viewPasswordClicked() {
        viewPassword(zichtbaarWachtwoord, nieuweWachtwoord);
        viewPassword(bevestigZichtbaarWachtwoord, bevestigWachtwoord);
    }
    //Deze methode roept de methode verificationResetService() aan.
    // Als deze methode true retourneert,
    // maakt het programma het nieuwe wachtwoordveld,
    // het bevestigingsveld, de wachtwoord-bekijken-checkbox,
    // het reset-knop icon en de resetknop beschikbaar.
    @Override
    @FXML
    void directFeedback() {
        try {
            if (getHuidigeGebruiker().isEmpty()) {
                ongEmail.setText("* geen e-mailadres ingevuld");
                email.setStyle(foutStyle);
                email.requestFocus();
            }if (!getHuidigeGebruiker().isEmpty()) {
                try (Reader reader = new FileReader(USER_FILE)) {
                    // JSON file reading
                    JsonArray users = JsonParser.parseReader(reader).getAsJsonArray();
                    // verification
                    for (int i = 0; i < users.size(); i++) {
                        JsonObject user = users.get(i).getAsJsonObject();
                        String storedUsername = user.get("E-mail").getAsString();
                        String storedPassword = user.get("Wachtwoord").getAsString();
                        if (storedUsername.equals(getHuidigeGebruiker())) {
                            System.out.println("gebruiker naam bestaat wel");
                            email.setStyle(goedGekeurdStyle);
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }if (getOudeWachtwoord().isEmpty()) {
                ongWachtw.setText("* geen e-mailadres ingevuld");
                oudeWachtwoord.setStyle(foutStyle);
                oudeWachtwoord.requestFocus();


            }if (!getOudeWachtwoord().isEmpty()) {
                if (verificationResetService(getHuidigeGebruiker(), getOudeWachtwoord())) {
                    System.out.println("Login  verified");
                    email.setStyle(goedGekeurdStyle);
                    oudeWachtwoord.setStyle(goedGekeurdStyle);
                    nieuweWachtwoord.setDisable(false);
                    zichtbaarWachtwoord.setDisable(false);
                    bevestigWachtwoord.setDisable(false);
                    bevestigZichtbaarWachtwoord.setDisable(false);
                    wachtwoordBekijken.setDisable(false);
                } else {
                    ongEmail.setText("Account is ons niet bekend");
                    email.setStyle(foutStyle);
                    oudeWachtwoord.setStyle(foutStyle);
                    System.out.println("Login not verified");

                }
            }

        }catch (NullPointerException e) {
            e.getMessage();
        }
    }
    //-reset alle fouts mededelen labels naar “null” als de gebruiker begint te typen.
    @Override
    void resetLabel() {
        ongEmail.setText("");
        email.setStyle(bassisStyle);

        ongWachtw.setText("");
        oudeWachtwoord.setStyle(bassisStyle);

        ongNieuWachtw.setText("");
        nieuweWachtwoord.setStyle(bassisStyle);

        ongBevWachtw.setText("");
        bevestigWachtwoord.setStyle(bassisStyle);

        ongeldigInvoer.setText("");
        nietOvereen.setText("");


    }
    //reset button clicked
    //Via een for-loop zoekt deze methode naar een overeenkomst met de opgegeven e-mail.
    //In het geval dat een overeenkomst wordt gevonden,
    //stelt het de nieuwe wachtwoord in plaats van het oude wachtwoord.
    public static void resetPassword(String email, String password) {
        HoofdController.users = loadUser();
        for (User user : HoofdController.users) {
            if (user.getEmail().equals(email)) {
                user.setPassword(password);
                saveUser();
            }
            return;
        }
    }
    //in het geval de gebruiker wil niet meer account resetten,
    //brengt het programma hem naar het inlog venster weer.
    @Override
    void backToHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) bevestigWachtwoord.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
