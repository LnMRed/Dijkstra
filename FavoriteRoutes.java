package com.example.ov;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;


public class FavoriteRoutes extends HoofdController implements Initializable {

    public Label resultLabel;
    public ImageView back;
    @FXML
    protected ImageView addRoute;
    @FXML
    protected GridPane favorietRoute;
    @FXML
    protected TextField routeName;
    @FXML
    protected ChoiceBox<String> vertrekpunt;
    @FXML
    protected ChoiceBox<String> bestemming;
    @FXML
    protected Label routeNameLabel;
    @FXML
    protected Label bestemmingLabel;
    @FXML
    protected Label vertrekpuntLabel;
    @FXML
    protected Label routeButtonLabel;
    HashMap<String, String> route;
    @FXML
    MenuButton favoriteRoute;
    @FXML
    protected MenuItem verwijderen;
    @FXML
    protected MenuItem aanpassen;
    protected static String selectedFavRoute;


    ///////////////////
    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) throws NullPointerException {
        //haalt  de stations namen in zet ze klaar in de choiceBoxes
        TransportLine line = new TransportLine("routeEen");
        vertrekpunt.getItems().addAll(String.valueOf(line.getStations()));
        bestemming.getItems().addAll(String.valueOf(line.getStations()));

    }

    //get favorite route
    @FXML
    public boolean addFavoriteRoute(String currentUserEmail, String routeName, String startPoint, String endPoint) {
        try {
            //eerst maakt nieuwe HashMap<> () “route”
            route = new HashMap<>();
            //pak alle gegevens van fxml-bestand en zet hun in de nieuwe hashMap “route”
            route.put("gebruiker", currentUserEmail);
            route.put("route naam", routeName);
            route.put("vertrekpunt", startPoint);
            route.put("bestemming", endPoint);
            //haalt de buffer route van json-bestand en zet ze in route ArrayList (ArrayList van HashMaps)
            routes = loudRoute();
            //Voeg de nieuwe route naar het routes ArrayList toe
            routes.add(route);
            System.out.println("Route added");
            System.out.println(routes);
            //bewaar het ArrayList als JsonArray in Json-bestand
            saveRoute();
            System.out.println("Routes saved");
            //returner true in het geval gelukt is anders false
            return true;
        }catch (NullPointerException e){
            System.err.println(e.getMessage());
        }
        return false;
    }

    /////////////////////////
    //loud favorite route
    public static ArrayList<HashMap<String, String>> loudRoute() {
        //Maak nieuwe HashMap ArrayList
        routes = new ArrayList<>();
        //Haalt het routes-bestand als nieuwe BufferedReader Object “reader”
        try (BufferedReader reader = new BufferedReader(new FileReader(ROUTES_FILE))) {
            //Maak JsonArray van de BufferedReader reader
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            //Maak For-loop voor elke element in JsonArray
            try {
                for (JsonElement jsonElement : jsonArray) {
                    //Maak nieuwe HashMap “newRoute
                    HashMap<String, String> newRoute = new HashMap<>();
                    ////Maak JsonElement als newRoute door for-loop
                    newRoute.put("gebruiker", jsonElement.getAsJsonObject().get("gebruiker").getAsString());
                    newRoute.put("route naam", jsonElement.getAsJsonObject().get("route naam").getAsString());
                    newRoute.put("vertrekpunt", jsonElement.getAsJsonObject().get("vertrekpunt").getAsString());
                    newRoute.put("bestemming", jsonElement.getAsJsonObject().get("bestemming").getAsString());
                    //Voeg newRoute toe routes (HashMaps ArrayList)
                    routes.add(newRoute);
                }
            }catch (UnsupportedOperationException e){
                System.err.println(e.getMessage());

            }

        } catch (IOException e) {
            System.err.println("Oops!\nroutes gegevens konen niet laden worden: " + e.getMessage());
        }
        return routes;
    }

    ///////////////////////
    //save favorite route
    public static void saveRoute() {
        //Maak nieuwe Object van klass ObjectMapper “mapper”
        ObjectMapper mapper = new ObjectMapper();
        try {
            //Schrijf HashMaps ArrayList “routes” op routes-Json-bestand
            mapper.writeValue(new File(ROUTES_FILE), routes);
        } catch (IOException e) {
            e.getStackTrace();
        }
        /////////////////////////////////////////////
        //ander manier om data te bewaren door json IPV jackson
        //////////////////////////////////////////
        /*try (BufferedWriter writer = new BufferedWriter(new FileWriter("routes.json"))) {
            JsonArray jsonArray = new JsonArray();
            for (HashMap<String,String> route : routes) {
                JsonObject jRoute = new JsonObject();
                jRoute.addProperty("gebruiker",route.get("gebruiker"));
                jRoute.addProperty("vertrekpunt",route.get("vertrekpunt"));
                jRoute.addProperty("bestemming",route.get("bestemming"));
                jsonArray.add(jRoute);
            }
            writer.write(jsonArray.toString());
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Oops!\nroutes gegevens konen niet laden worden: " + e.getMessage());
        }*/
    }

    @FXML
    void onFavoriteBookClicked() {
        //Haalt routes door getFavoriteRoutes methode op basses van de huidige gebruiker gegevens
        routes = User.getFavoriteRoutes(getCurrentUser());
        //Defienteer het variable int x = 3 voor later om wisselende row index te krijgen vanaf row 3
        int x = 3;
        //in het geval routes niet leeg maakt for-loop voor elke route in routes
        if (!routes.isEmpty()) {
            for (HashMap<String, String> route : routes) {
                //Genereert nieuwe MenuButton van elke route en geef hem de route naam als text.
                favoriteRoute = new MenuButton(route.get("route naam"));
                //Haalt bestaande choiceBox effect en geef hem naar de nieuwe button
                favoriteRoute.setEffect(vertrekpunt.getEffect());
                //Haalt bestaande choiceBox Style en geef hem naar de nieuwe button
                favoriteRoute.setStyle(vertrekpunt.getStyle());
                //Zet methode #onFavRouteClicked als  event OnMouseClicked in route MenuButton
                favoriteRoute.setOnMouseClicked(this::onFavRouteClicked);
                //Zet twee nieuwe MenuItems in de route button (verwijderen en aanpassen)
                favoriteRoute.getItems().addAll(verwijderen = new MenuItem("verwijderen"), aanpassen = new MenuItem("aanpassen"));
                //Zet methode #onVerwijderenClicked als event OnAction in verwijderen MenuItem
                verwijderen.setOnAction(this::onVerwijderenClicked);
                //Zet methode #onAanpassenClicked als event OnAction in aanpassenMenuItem
                aanpassen.setOnAction(this::onAanpassenClicked);
                //Geef elke route vast column index (2) en wisselende row index (x)
                favorietRoute.add(favoriteRoute, 2, x);
                //Row index (x) plus 1 voor volgende loop
                ++x;
            }
        } else {
            Label geenRoutes = new Label("* Er zijn geen favoriet routes");
            favorietRoute.add(geenRoutes, 2, 4);
            geenRoutes.setStyle("-fx-font-family: 'Times New Roman'; -fx-text-fill:  rgba(255,119,112,0.7);-fx-font-size: 14;");

        }
    }

    @FXML
    private void onFavRouteClicked(MouseEvent event) {
        System.out.println("Event selected route triggered");
        //Haalt de route naam van de route MenuButton
        MenuButton menuButton = (MenuButton) event.getSource();
        //initialiseer route naam in String variabl “selectedFavRoute
        selectedFavRoute = menuButton.getText();
        System.out.println("current favorite route : " + selectedFavRoute);
    }

    @FXML
    void onVerwijderenClicked(ActionEvent event) {
        System.out.println("Event verwijderen triggered");
        //Voor elke route in routes zoek op de gegevende route naam, als het is gevonden
        for (HashMap<String, String> route : routes) {
            if (route.get("route naam").equals(selectedFavRoute)) {
                //roep de method deletWarning() om eerst bevestiging te krijgen voor dat verwijderd worden.
                deleteWarning(route);
            }
        }
    }

    @FXML
    void onAanpassenClicked(ActionEvent event) {
        System.out.println("Event aanpassen triggered");
        //Voor elke route in routes zoek op de gegevende route naam, als het  gevonden is:
        for (HashMap<String, String> route : routes) {
            if (route.get("route naam").equals(selectedFavRoute)) {
                //Haalt de route naam en zet hem in de TextField “route naam”
                routeName.setText(route.get("route naam"));
                //Haalt de route vertrekpunt en zet hem in de ChoiceBox “vertrekpunt”
                vertrekpunt.setValue(route.get("vertrekpunt"));
                //Haalt de route bestemming en zet hem in de ChoiceBox “bestemming”
                bestemming.setValue(route.get("bestemming"));
                //Verandeer de addRoute ImageView naar checked icon
                addRoute.setImage(new Image("/checked.png"));
                //Verandeer de routeButtonLabel naar “route aanpassen”
                routeButtonLabel.setText("route aanpassen");
                //Zet de methode routeAanpassen als event OnMouseClicked in de nieuwe ImageView
                addRoute.setOnMouseClicked(this::routeAanpassen);

            }
        }


    }

    void routeAanpassen(MouseEvent event) {
        //replace all (route naam, vertrekpunt, bestemming )
        //met de nieuwe gegevens die komt uit fxml velden.
        for (HashMap<String, String> route : routes) {
            if (route.get("route naam").equals(selectedFavRoute)) {
                route.replace("route naam", routeName.getText());
                route.replace("vertrekpunt", vertrekpunt.getValue());
                route.replace("bestemming", bestemming.getValue());
            }
        }
        saveRoute();
        System.out.println(selectedFavRoute + "route is aangepast");
        refresh();
    }

    void deleteWarning(HashMap<String, String> route) {
        //Alert window
        //Maak nieuwe aler object als Confirmation alerType
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //Geef hem title “confirmatie”
        alert.setTitle("Confirmatie");
        //Geef hem Hoofd Text
        alert.setHeaderText("verwijderen Confirmatie");
        //Geef hem mededeling
        alert.setContentText("Ben u zeker dat je dit route willen verwijderen?");
        //add options
        //Maak twee buttons type (verwijderen, annuleren)
        ButtonType buttonTypeYes = new ButtonType("Verwijderen");
        ButtonType buttonTypeNo = new ButtonType("Annuleren");
        //Zet de nieuwe buttonsType in alertObject
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        //user choice
        //Vraag de gebruiker en haal een result ervan
        Optional<ButtonType> result = alert.showAndWait();
        //Wiseel result
        if (result.isPresent() && result.get() == buttonTypeYes) {
            routes.remove(route);
            saveRoute();
            System.out.println("route is verwijdeerd");
            refresh();
        } else {
            System.out.println("verwijderen is annuleerd");
            refresh();
        }
    }

    @FXML
    void onAddClicked() {
        try {
            //In het geval de velden niet leeg zijn:
            if (!routeName.getText().isEmpty() && vertrekpunt.getValue().isEmpty() && !bestemming.getValue().isEmpty()) {
                //Haalt reageren (true of false) van methode addFavoriteRoute() op basses van de gegeven gegevens in fxml velden
                if (addFavoriteRoute(getCurrentUser().getEmail(), routeName.getText(), vertrekpunt.getValue(), bestemming.getValue())) {
                    //Als true voeg het toe en laat de “doneBox” zichtbaar
                    doneBox.setVisible(true);
                    refresh();
                }
                System.out.println("Gebruiker: " + getCurrentUser().getEmail());
                System.out.println("vertrekpunt: " + vertrekpunt.getValue());
                System.out.println("Bestemming: " + bestemming.getValue());
                //Anders wijs naar de fout
            }else{

                if (routeName.getText().isEmpty()) {
                    routeNameLabel.setText("* geen route naam");
                    routeName.setStyle(foutStyle);
                }
                if (vertrekpunt.getValue().isEmpty()) {
                    vertrekpuntLabel.setText("* geen vertrekpunt naam");
                    vertrekpunt.setStyle(foutStyle);
                }
                if (bestemming.getValue().isEmpty()) {
                    bestemmingLabel.setText("* geen bestemming naam");
                    bestemming.setStyle(foutStyle);
                }
            }
            //Open de vester weer

        }catch (Exception e) {
            System.err.println(e.getMessage());
            doneLabel.setText("* ongeldig invoer ");
        }

    }
    //reset alle fouts mededelen labels naar “null” als de gebruiker begint te typen.
    @Override
    void resetLabel() {
        doneBox.setVisible(false);
        routeNameLabel.setText("");
        routeName.setStyle(bassisStyle);
        vertrekpuntLabel.setText("");
        vertrekpunt.setStyle(bassisStyle);
        bestemmingLabel.setText("");
        bestemming.setStyle(bassisStyle);

    }
    //in het geval de gebruiker wil niet meer favoriet routes  beheren, brengt het programma hem naar het TravelHome venster weer.
    @Override
    void backToHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("TravelHome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) bestemming.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    void refresh() {
        //Ruim de parent container op.
        favorietRoute.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("favoriteRoutes.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.<Parent>load(), 1000, 700);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) favorietRoute.getScene().getWindow();
        stage.setScene(scene);
        //Open het venster weer
        stage.show();
    }


}
