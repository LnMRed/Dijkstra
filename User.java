package com.example.ov;

import com.google.gson.*;
import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.ov.HoofdController.USER_FILE;
import static com.example.ov.HoofdController.users;


public class User extends HoofdController {


    private static User currentUser;
    private String firstName;
    private String lastName;
    private String email;
    private String password;


    User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

    }

    public User() {

    }

    public static void setCurrentUser(String firstName, String lastName, String email, String password) {
        User.currentUser = new User(firstName, lastName, email, password);
    }

    ////////////////////////////////////////////////////////////////
    public static User getCurrentUser() {
        return currentUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static ArrayList<HashMap<String, String>> getFavoriteRoutes(User user) {
        ArrayList<HashMap<String, String>> routes = FavoriteRoutes.loudRoute();
        favoriteRoutes = new ArrayList<>();
        for (HashMap<String, String> favoriteRoute : routes) {
            if (favoriteRoute.get("gebruiker").equals(user.getEmail())) {
                favoriteRoutes.add(favoriteRoute);
            }
        }
        return favoriteRoutes;
    }
    ///////////////////////////////////////////////////////////////

    //get new user
    static void addUser(User user) {
        //Haalt de nieuwe gebruiker gegevens uit het FXML-bestand.
        //maak ArrayList van de gegevens die bestaat in Json-bestand

        HoofdController.users = loadUser();
        //maakt nieuwe object vervolgens voeg hem naar het User ArrayList,
        HoofdController.users.add(user);
        //eindelijk bewaar het op Json-bestand
        saveUser();
    }

    //load user data
    static ArrayList<User> loadUser() throws NullPointerException {

        //Maak eerst een JSON-bestand als een nieuw object van de klasse BufferedReader.
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            //Maak een nieuwe ArrayList<User>
            users = new ArrayList<>();
            //Haal de gegevens van het JSON-bestand op als een JsonArray
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            //Gebruik een for-loop om voor elk element in het JsonArray de voornaam,
            //achternaam, e-mail en wachtwoord op te halen
            for (JsonElement jsonElement : jsonArray) {
                String firstName = jsonElement.getAsJsonObject().get("Voornaam").getAsString();
                String lastName = jsonElement.getAsJsonObject().get("Achternaam").getAsString();
                String email = jsonElement.getAsJsonObject().get("E-mail").getAsString();
                String password = jsonElement.getAsJsonObject().get("Wachtwoord").getAsString();
                //Maak een Java-object van de User-klasse.
                HoofdController.users.add(new User(firstName, lastName, email, password));
            }

        } catch (IllegalStateException | FileNotFoundException e) {
            System.err.println(e.getMessage());
            /*return HoofdController.users = new ArrayList<>();*/
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);

        }
        return HoofdController.users;
    }

    //Save user data
    static void saveUser() {
        //Maak eerst een JSON-bestand als een nieuw object van de klasse BufferedWriter.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            //Maak een nieuwe JsonArray
            JsonArray jsonArray = new JsonArray();
            //Gebruik een for-loop om voor elk element in het ArrayList de voornaam,
            //achternaam, e-mail en wachtwoord op te halen
            for (User user : HoofdController.users) {
                //Voeg die element toe JsonArray
                jsonArray.add(getJsonObject(user));
            }
            //Schrijf het JsonArray  in Json-bestand
            writer.write(jsonArray.toString());
            //Maak niuewe lijn
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Oops!\nGebruikers gegevens konen niet laden worden: " + e.getMessage());
        }
    }

    //make java object as Json object
    private static @NotNull JsonObject getJsonObject(User user) {
        //Maak nieuwe jsonObject
        JsonObject jUser = new JsonObject();
        //Voegtoe alle property (voornaam, achternaam, email, wachtwoord)
        jUser.addProperty("Voornaam", user.getFirstName());
        jUser.addProperty("Achternaam", user.getLastName());
        jUser.addProperty("E-mail", user.getEmail());
        jUser.addProperty("Wachtwoord", user.getPassword());
        //returneer JsonObject jUser
        return jUser;
    }


}