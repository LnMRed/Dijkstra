package com.example.ov;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

import java.util.ResourceBundle;

public class TravelHomeController extends HoofdController implements Initializable {
    int i= 0;
    @FXML
    protected GridPane travelHomeGrid;
    @FXML
    public ImageView route;
    @FXML
    public ImageView bus;
    @FXML
    public ImageView train;
    @FXML
    public ImageView tram;
    @FXML
    public ImageView favoriteIcon;
    @FXML
    protected ImageView inloggen;
    @FXML
    protected ImageView uitloggen;
    @FXML
    protected Label userName;
    @FXML
    public ImageView Nederland = new ImageView();
    @FXML
    public Label uitlogLabel;
    @FXML
    public Label inlogLabel;
    @FXML
    public Label favoriteLabel;
    @FXML
    public Label distanceLabel;
    @FXML
    protected Label routeResult;
    @FXML
    protected Label Station1;
    @FXML
    protected Label Station2;
    @FXML
    protected ChoiceBox<String> vertrekpunt;
    @FXML
    protected ChoiceBox<String> bestemming;

    @FXML
    protected ChoiceBox<String> departureHour;

    @FXML
    protected ChoiceBox<String> departureMinute;

    @FXML
    protected ChoiceBox<String> arrivalHour;

    @FXML
    protected ChoiceBox<String> arrivalMinute;

    @FXML
    protected Label TravelTime;
    @FXML
    protected  Label ArrivalTime;

    @FXML
    protected Canvas routeCanvas;

    @FXML
    protected Label favRoute;
    private boolean isBusSelected = false;
    private boolean isTrainSelected = false;
    private boolean isTramSelected = false;

    public Button addTransferButton;
    public VBox choiceBoxContainer; // transfer stops worden hierin gedaan
    //list of transfer choice boxes to hold transfer stops
    private final List<ChoiceBox<String>> transferChoiceBoxes = new ArrayList<>();
    /////////
    private final Map<ChoiceBox<String>, Mapping.CircleData> drawnCircles = new HashMap<>();

    private TransportNetwork network;
    private List<String> transferstops;

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) throws NullPointerException {

        // moet in zn eigen ding om treinlijnen te initializen
        initializeNetwork();
        // ding met hashmap die lines integreert

        initializeTime();

        vertrekpunt.getItems().addAll(network.getStations());
        bestemming.getItems().addAll(network.getStations());

        // Initialize the drawn circles map
        drawnCircles.put(vertrekpunt, null);
        drawnCircles.put(bestemming, null);

        String imagePath = "/Nederland.jpg";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        Nederland.setImage(image);

        double scaleX = routeCanvas.getWidth() / image.getWidth();
        double scaleY = routeCanvas.getHeight() / image.getHeight();

        // Choose the smaller scaling factor to ensure the image fits within the canvas
        double scale = Math.min(scaleX, scaleY);

        // Calculate the scaled dimensions
        double scaledWidth = image.getWidth() * scale;
        double scaledHeight = image.getHeight() * scale;

        // Draw the scaled image onto the canvas
        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, scaledWidth, scaledHeight);
    //////////////////////////////////////////////////////////////////////////
        //Controleer of er een huidige gebruiker is ingelogd.
        // Indien dit het geval is, past het programma zich aan om de naam van de gebruiker en zijn favoriete route voor hem klaar te zetten.
        if (getCurrentUser() != null) {
            userName.setText(User.getCurrentUser().getFirstName());
            inloggen.setDisable(true);
            inloggen.setImage(new Image("/tour-guide.png"));
            uitloggen.setDisable(false);
            favoriteIcon.setDisable(false);
            favoriteRoutes = User.getFavoriteRoutes(currentUser);
            int x = 2;
            for (HashMap<String, String> favoriteRoute : favoriteRoutes) {
                if (x <= 5) {
                    favRoute = new Label(favoriteRoute.get("route naam"));
                    favRoute.setOnMouseClicked(this::choseFavRoute);
                    travelHomeGrid.add(favRoute, x, 0);
                    favRoute.setCursor(Cursor.HAND);
                    favRoute.setStyle("-fx-translate-x: 10;-fx-font-family: 'Times New Roman';-fx-font-size: 16;-fx-text-fill: #344b61;-fx-background-color: #b3d7f8;-fx-background-radius: 50;-fx-padding: 3;");
                    x++;
                    System.out.println(favoriteRoute.get("route naam"));
                }
            }
        }
    ////////////////////////////////////////////////////////////////////////
    }


    private void initializeTime() {
        LocalTime currentTime = LocalTime.now();

        // Populate departure hour choice box based on timetable
        ObservableList<String> departureHourOptions = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            String hour = String.format("%02d", i);
                departureHourOptions.add(hour);
        }
        departureHour.setItems(departureHourOptions);
        departureHour.setValue(String.format("%02d", currentTime.getHour()));

        // Populate departure minute choice box
        ObservableList<String> departureMinuteOptions = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            departureMinuteOptions.add(String.format("%02d", i));
        }
        departureMinute.setItems(departureMinuteOptions);
        departureMinute.setValue(String.format("%02d", currentTime.getMinute()));

        // Populate arrival hour choice box
        ObservableList<String> arrivalHourOptions = FXCollections.observableArrayList();
        arrivalHourOptions.addAll(departureHourOptions);
        arrivalHour.setItems(arrivalHourOptions);

        // Populate arrival minute choice box
        ObservableList<String> arrivalMinuteOptions = FXCollections.observableArrayList();
        arrivalMinuteOptions.addAll(departureMinuteOptions);
        arrivalMinute.setItems(arrivalMinuteOptions);
    }

    private void initializeNetwork()
    {
        LocalTime current = LocalTime.now();
        String departHour = (departureHour.getValue() != null) ? departureHour.getValue() : String.valueOf(current.getHour());
        String departMinute = (departureMinute.getValue() != null) ? departureMinute.getValue() : String.valueOf(current.getMinute());

        network = new TransportNetwork(mapping);
        Timetable timetable = new Timetable();



        TransportLine line1 = new TransportLine("Line 1");
        line1.addStation(new Station("Amsterdam Centraal", LocalTime.of(7, 0)));
        line1.addStation(new Station("Amsterdam Zuid", LocalTime.of(8, 0)));
        line1.addStation(new Station("Utrecht Centraal", LocalTime.of(9, 0)));
        line1.addStation(new Station("Amersfoort Centraal", LocalTime.of(10, 0)));
        line1.addStation(new Station("Amersfoort Schothorst", LocalTime.of(11, 0)));

        timetable.makeTrainsRun(line1);

        TransportLine line2 = new TransportLine("Line 2");
        line2.addStation(new Station("Utrecht Centraal", LocalTime.of(9, 0)));
        line2.addStation(new Station("Limburg", LocalTime.of(11, 0)));

        timetable.makeTrainsRun(line2);

        TransportLine line3 = new TransportLine("Line 3");
        line3.addStation(new Station("Utrecht Centraal", LocalTime.of(8, 30)));
        line3.addStation(new Station("Amersfoort Centraal", LocalTime.of(9, 0)));
        line3.addStation(new Station("Amersfoort Schothorst", LocalTime.of(9, 15)));

        timetable.makeTrainsRun(line3);

        network.setTimetable(timetable);

        network.addTransportLine(line1);
        network.addTransportLine(line2);
        network.addTransportLine(line3);

        if (departHour.length() == 1) {
            departHour = "0" + departHour;
        }
        if (departMinute.length() == 1) {
            departMinute = "0" + departMinute;
        }

        String departureTime = departHour + ":" + departMinute;
        System.err.println(departureTime);

        Map<String, Map<String, Integer>> graph = new HashMap<>();

        List<String> stations1 = line1.getStations();
        for (int i = 1; i < stations1.size(); i++) {
            String currentStation = stations1.get(i);
            String previousStation = stations1.get(i - 1);
            network.edging(graph, currentStation, previousStation, departureTime );
        }

        network.setTimetable(timetable);

        network.addTransportLine(line1);
        network.addTransportLine(line2);
        network.addTransportLine(line3);
    }
///////////////////////////////////////////////////////////////////////
    //herkent de favoriete route die de gebruiker op gedrukt,
    //dan zet het klaar voor hem het station velden.
    @FXML
    void choseFavRoute(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        String selectedFavRoute = clickedLabel.getText();
        for (HashMap<String, String> favoriteRoute : favoriteRoutes) {
            if (favoriteRoute.get("route naam").equals(selectedFavRoute)) {
                String selectedVertrekpunt = favoriteRoute.get("vertrekpunt");
                String selectedBestemming = favoriteRoute.get("bestemming");
                vertrekpunt.getSelectionModel().select(selectedVertrekpunt);
                bestemming.getSelectionModel().select(selectedBestemming);
            }
        }
    }
    //In het geval dat de gebruiker wil inloggen,
    //brengt het programma hem naar het inlogvenster,
    //wanneer hij op de inlogknop drukt.
    @FXML
    protected void inloggenOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) routeResult.getScene().getWindow();
        stage.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }
    //Laat het label van de inlogknop,
    //favorietknop en uitlogknop oplichten,
    //zolang de cursor eroverheen beweegt.
    @Override
    void showLabelEvent() {
        uitlogLabel.setVisible(uitloggen.isHover());
        favoriteLabel.setVisible(favoriteIcon.isHover());
        inlogLabel.setVisible(inloggen.isHover());
    }
    //In het geval dat de gebruiker wil zijn favoriete route beheren,
    // brengt het programma hem naar het favoriete route venster wanneer hij op de favorietknop drukt.
    @FXML
    protected void favouriteIconClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("favoriteRoutes.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) routeCanvas.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /////////////////
    //controleer wat voor vervoersmiddel is gekeesd.
    @FXML
    protected void onTransportModeChanged() {
        isBusSelected = bus.isPickOnBounds();
        isTramSelected = tram.isPickOnBounds();
        isTrainSelected = train.isPickOnBounds();
    }
    ///////////////////////////////////
    List<String> pain()
    {
        List<String> transferStops = new ArrayList<>();
        transferStops.add("Utrecht Centraal");
        transferStops.add("Amersfoort Centraal");
        return transferStops;
    }

    @FXML
    protected void onRouteButtonClick() {
        String selectedStation1 = vertrekpunt.getValue();
        String selectedStation2 = bestemming.getValue();

        String departHour = departureHour.getValue();
        String departMinute = departureMinute.getValue();

        if (departHour.length() == 1) {
            departHour = "0" + departHour;
        }
        if (departMinute.length() == 1) {
            departMinute = "0" + departMinute;
        }

        String DepartureTime = departHour + ":" + departMinute;

        LocalTime estimatedTime = network.calculateArrivalTime(selectedStation1, selectedStation2, DepartureTime);
        double traveltime = network.calcDist(selectedStation1, selectedStation2);
        ArrivalTime.setText((int) Math.floor(traveltime) + " minutes");
        TravelTime.setText(String.valueOf(estimatedTime )); // moet omgewisseld worden
        if (selectedStation1 == null || selectedStation2 == null) {
            System.err.println("Fout: Geen stations gekozen.");
            return;
        }

        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        clearCanvas(gc);
        drawRoute(gc);

        List<String> transferStops = getSelectedTransferStops();
        List<String> transfers = pain();
        List<String> routeStations;

        try {
            if (transferStops.isEmpty()) {
                routeStations = network.calculateRoute(selectedStation1, selectedStation2, DepartureTime, transfers); //departime
            } else {
                routeStations = network.calculateRouteWithTransfer(selectedStation1, selectedStation2, transferStops, DepartureTime);
            }

            if (routeStations != null) {
                double totalDistance = drawRouteOnCanvas(gc, routeStations);
                distanceLabel.setText(String.format("Afstand: %.1f kilometer", totalDistance));
                routeResult.setText("De route is getekend\n" + "Route: " + routeStations);
                System.out.println("Route: " + routeStations);
            } else {
                System.err.println("Fout: Kan geen route berekenen.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fout bij het berekenen van de route: " + e.getMessage());
        }
    }

    private List<String> getSelectedTransferStops() {
        List<String> transferStops = new ArrayList<>();
        for (ChoiceBox<String> transferChoiceBox : transferChoiceBoxes) {
            String selectedTransferStop = transferChoiceBox.getValue();
            if (selectedTransferStop != null) {
                transferStops.add(selectedTransferStop);
            }
        }
        return transferStops;
    }

    private double drawRouteOnCanvas(GraphicsContext gc, List<String> routeStations) {
        double totalDistance = 0.0;
        Point2D previousStationCoordinates = null;

        for (int i = 0; i < routeStations.size(); i++) {
            String station = routeStations.get(i);
            Point2D stationCoordinates = mapping.getStationCoordinates().get(station);
            if (previousStationCoordinates != null) {
                double distance = mapping.calculateDistanceBetweenStations(routeStations.get(i - 1), station) / 4.2;
                totalDistance += distance;
                drawTravelLine(gc, previousStationCoordinates, stationCoordinates);
            }
            previousStationCoordinates = stationCoordinates;
        }
        return totalDistance;
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.clearRect(0, 0, routeCanvas.getWidth(), routeCanvas.getHeight());
    }

    private void drawRoute(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, routeCanvas.getWidth(), routeCanvas.getHeight());

        double imageWidth = Nederland.getImage().getWidth();
        double imageHeight = Nederland.getImage().getHeight();
        double scaleX = routeCanvas.getWidth() / imageWidth;
        double scaleY = routeCanvas.getHeight() / imageHeight;
        double scale = Math.min(scaleX, scaleY);
        // Calculate the scaled dimensions
        double scaledWidth = imageWidth * scale;
        double scaledHeight = imageHeight * scale;
        gc.drawImage(Nederland.getImage(), 0, 0, scaledWidth, scaledHeight);
        redrawDots(scale, gc);
    }

    private void drawTravelLine(GraphicsContext gc, Point2D station1Coordinates, Point2D station2Coordinates) {
        if (station1Coordinates != null && station2Coordinates != null) {
            double scaleX = routeCanvas.getWidth() / Nederland.getImage().getWidth();
            double scaleY = routeCanvas.getHeight() / Nederland.getImage().getHeight();
            double scale = Math.min(scaleX, scaleY);

            double station1X = station1Coordinates.getX() * scale;
            double station1Y = station1Coordinates.getY() * scale;
            double station2X = station2Coordinates.getX() * scale;
            double station2Y = station2Coordinates.getY() * scale;

            if (isBusSelected) {
                gc.setStroke(Color.GREEN); // Change color for bus route
            } if (isTrainSelected) {
                gc.setStroke(Color.BLUE); // Change color for train route
            } if (isTramSelected) {
                gc.setStroke(Color.YELLOW); // Change color for tram route
            } else {
                gc.setStroke(Color.PURPLE);
            }
            gc.setLineWidth(5);
            gc.strokeLine(station1X, station1Y, station2X, station2Y);
        } else {
            System.out.println("One or both station coordinates are null.");
        }
    }

    private void redrawDots(double scale, GraphicsContext gc) {
        for (Map.Entry<ChoiceBox<String>, Mapping.CircleData> entry : drawnCircles.entrySet()) {
            Mapping.CircleData circleData = entry.getValue();
            if (circleData != null) {
                gc.setFill(circleData.getColor());
                gc.fillOval(circleData.x() * scale, circleData.y() * scale, 10, 10);
            }
        }
    }

    private void drawCircle(double x, double y, Color color) {
        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillOval(x, y, 10, 10);
    }

    private void redrawCircles(String selectedStation) {
        GraphicsContext gc = routeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, routeCanvas.getWidth(), routeCanvas.getHeight()); // Clear the canvas
        // Draw the background image
        drawRoute(gc);
        // Redraw all circles
        for (Map.Entry<ChoiceBox<String>, Mapping.CircleData> entry : drawnCircles.entrySet()) {
            Mapping.CircleData circleData = entry.getValue();
            if (circleData != null) {
                // get correct city coordinates
                Point2D coordinates = mapping.getStationCoordinates().get(selectedStation);
                double x = coordinates.getX();
                double y = coordinates.getY();
                drawCircle(x, y, circleData.getColor());
            }
        }
    }

    @FXML
    public void getCities(ActionEvent event) {
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) event.getSource();
        String selectedStation = choiceBox.getValue();
        if (selectedStation != null) {
            Point2D coordinates = mapping.getStationCoordinates().get(selectedStation);
            double x = coordinates.getX();
            double y = coordinates.getY();
            Color circleColor;
            if (choiceBox.equals(vertrekpunt) || choiceBox.equals(bestemming)) {
                circleColor = choiceBox.equals(vertrekpunt) ? Color.RED : Color.BLUE;
            } else {
                System.out.println("done");
                circleColor = Color.GREEN;
            }
            drawnCircles.put(choiceBox, new Mapping.CircleData(x, y, circleColor));
            redrawCircles(selectedStation);
            if (choiceBox.equals(vertrekpunt)) {
                Station1.setText(selectedStation);
            } else if (choiceBox.equals(bestemming)) {
                Station2.setText(selectedStation);
            }
        }
    }

    //In het geval dat de gebruiker wil uitloggen,
    //brengt het programma hem naar het inlogvenster weer wanneer hij op de uitlogknop drukt.
    @Override
    void uitloggen() throws NullPointerException, IOException {
        setCurrentUser(null);
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) routeCanvas.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void onAddTransferStop() // going to add the choicebox to hold transfer stops
    {
        if (i < 4) {
            int x =6;
            ChoiceBox<String> transferChoiceBox = new ChoiceBox<>();
            transferChoiceBox.getItems().addAll((network.getStations()));
            travelHomeGrid.add(transferChoiceBox,0,x);// top right bottom left
            transferChoiceBox.setOnAction(this::getCities);
            transferChoiceBox.setStyle(vertrekpunt.getStyle());
            transferChoiceBox.setEffect(vertrekpunt.getEffect());
            choiceBoxContainer.getChildren().add(transferChoiceBox);
            transferChoiceBoxes.add(transferChoiceBox);
            ++x;
            ++i;
        }
    }
}

