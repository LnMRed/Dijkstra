package com.example.ov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.*;
import java.util.Objects;

public class OvApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OvApplication.class.getResource("TravelHome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        //scene.getStylesheets().add(String.valueOf(getClass().getResource("/styles.css")));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/train.png")));
        stage.getIcons().add(icon);
        stage.setTitle("TravelApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
