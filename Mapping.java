package com.example.ov;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class Mapping {
    // maps coordinates
    private final Map<String, Point2D> stationCoordinates = new HashMap<>();

    public Mapping() {
        stationCoordinates.put("Amsterdam Centraal", new Point2D(620, 750));
        stationCoordinates.put("Amsterdam Zuid", new Point2D(620, 766));
        stationCoordinates.put("Utrecht Centraal", new Point2D(700, 920));
        stationCoordinates.put("Amersfoort Centraal", new Point2D(790, 884));
        stationCoordinates.put("Amersfoort Schothorst", new Point2D(800, 900));
        stationCoordinates.put("Limburg", new Point2D(900, 1400));
    }

    public double calculateDistanceBetweenStations(String station1, String station2) {
        Point2D point1 = stationCoordinates.get(station1);
        Point2D point2 = stationCoordinates.get(station2);
        return calculateDistance(point1, point2);
    }

    public Map<String, Point2D> getStationCoordinates() {
        return stationCoordinates;
    }

    private double calculateDistance(Point2D point1, Point2D point2) { // sqrt kan weg vgm
        return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2));
    }

    public record CircleData(double x, double y, Color color) {
        public Color getColor() {
            return this.color;
        }
    }
}

