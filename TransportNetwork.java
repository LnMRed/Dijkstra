package com.example.ov;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TransportNetwork {

    private final Mapping mapping;
    private final Map<String, TransportLine> transportLineMap = new HashMap<>(); // zo meerdere routes

    public TransportNetwork(Mapping mapping) {
        this.mapping = mapping;
    }


    public void addTransportLine(TransportLine line)
    {
        transportLineMap.put(line.getName(), line);
    }

    public Set<String> getStations() {
        Set<String> stations = new HashSet<>();
        for (TransportLine line : transportLineMap.values()) {
            stations.addAll(line.getStations());
        }
        return stations;
    }

    List<String> calculateRoute(String startStation, String endStation, String departureTime, List<String>transferStops) // moet tijd ook krijgen
    { // dit is dijkstra
        Map<String, Map<String, Integer>> graph = createGraph(departureTime);
        return dijkstra(graph, startStation, endStation, transferStops);
    }

    private Map<String, Map<String, Integer>> createGraph(String departureTime)
    {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for(TransportLine line: transportLineMap.values())
        {
            List<String> stations = line.getStations();
            for(int i = 0; i < stations.size(); i++) // increment for amount of stations
            {
                String currentStation = stations.get(i);
                graph.putIfAbsent(stations.get(i), new HashMap<>());
                if(i > 0)
                {
                    String previousStation = stations.get(i - 1);
                    edging(graph, currentStation, previousStation, departureTime);
                }
                if(i < stations.size()-1)
                {
                    String nextStation = stations.get(i + 1);
                    edging(graph, currentStation, nextStation, departureTime);
                }
            }
        }
        return graph;
    }

    void edging(Map<String, Map<String, Integer>> graph, String currentStation
                , String adjacentStation, String departureTime ) {

        String formattedDepartureTime = departureTime.length() == 4 ? "0" + departureTime : departureTime;
        //otherwise the date crashes it

        int travelTime = getTravelTime(currentStation, adjacentStation, formattedDepartureTime);
        graph.computeIfAbsent(currentStation, k -> new HashMap<>()).put(adjacentStation, travelTime);
        graph.computeIfAbsent(adjacentStation, k -> new HashMap<>()).put(currentStation, travelTime);
        System.out.println("Connecting " + currentStation + " to " + adjacentStation + " with travel time " + travelTime);
    }

    public int getTravelTime(String currentStation, String adjacentStation, String departureTime) {
        int speed = 2; // Example: 2 minutes per kilometer
        if (departureTime == null) {
            // Handle the case where departure time is null
            return 0; // Or any default value that makes sense in your application
        }
        // Get the distance between currentStation and adjacentStation
        double distance = mapping.calculateDistanceBetweenStations(currentStation, adjacentStation);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime departure = LocalTime.parse(departureTime, formatter);
        // Calculate the travel time based on the speed and distance
        int travelTime = (int) (speed * distance);
        // Calculate arrival time by adding travel time to departure time
        LocalTime arrivalTime = departure.plusMinutes(travelTime);
        return arrivalTime.getHour() * 60 + arrivalTime.getMinute();
    }

    private List<String> dijkstra(Map<String, Map<String, Integer>> graph, String startStation, String endStation, List<String> transferStops) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> stack = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Initialize distances
        for (String vertex : graph.keySet()) {
            distances.put(vertex, vertex.equals(startStation) ? 0 : Integer.MAX_VALUE);
            stack.add(vertex);
        }

        String current = null;
        while (!stack.isEmpty()) {
            current = stack.poll();
            if (current.equals(endStation))
                break;
            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                int newDistance = distances.get(current) + neighbor.getValue();
                if (newDistance < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDistance);
                    previous.put(neighbor.getKey(), current);
                    stack.add(neighbor.getKey());
                }
            }
        }
        List<String> path = new ArrayList<>();
        if (transferStops != null && !transferStops.isEmpty()) {
            for (String transferStop : transferStops) {
                List<String> partialRoute1 = dijkstra(graph, startStation, transferStop, new ArrayList<>());
                List<String> partialRoute2 = dijkstra(graph, transferStop, endStation, new ArrayList<>());
                if (!partialRoute1.isEmpty() && !partialRoute2.isEmpty()) {
                    path.addAll(partialRoute1.subList(0, partialRoute1.size() - 1));
                    path.addAll(partialRoute2);
                    return path;
                }
            }
        }

        while (previous.containsKey(current)) {
            path.addFirst(current);
            current = previous.get(current);
        }
        if (!path.isEmpty()) {
            path.addFirst(startStation);
        }
        return path;
    }


    public List<String> calculateRouteWithTransfer(String startStation, String endStation, List<String> transferStops, String departureTime) {
        Map<String, Map<String, Integer>> graph = createGraph(departureTime);

        List<String> route = new ArrayList<>();
        String currentStart = startStation;
        for (String transferStop : transferStops) {
            List<String> partialRoute = dijkstra(graph, currentStart, transferStop, transferStops);
            if (partialRoute.isEmpty()) {

                return Collections.emptyList(); // If any part of the route fails, return empty
            }
            route.addAll(partialRoute.subList(0, partialRoute.size() - 1));
            currentStart = transferStop;
        }
        List<String> finalPartialRoute = dijkstra(graph, currentStart, endStation, transferStops);
        if (!finalPartialRoute.isEmpty()) {
            route.addAll(finalPartialRoute);
        }
        return route;
    }

    public void setTimetable(Timetable timetable)
    {
    }

    public double calcDist(String selectedStation1, String selectedStation2) // time
    {
        double distance =  mapping.calculateDistanceBetweenStations(selectedStation1, selectedStation2);
        int speed = 5; // 2 kilometer per minute
        return  (distance / speed);
    }

    private LocalTime roundToQuarterHour(LocalTime time) {
        int minute = time.getMinute();
        int newMinute;

        if (minute < 15) {
            newMinute = 15;
        } else if (minute < 30) {
            newMinute = 30;
        } else if (minute < 45) {
            newMinute = 45;
        } else {
            newMinute = 0;
        }

        if (newMinute == 0 && minute >= 45) {
            time = time.plusHours(1);
        }
        // Adjust the hour if we round up to the next hour
        return time.withMinute(newMinute).withSecond(0).withNano(0);
    }

    public LocalTime calculateArrivalTime(String selectedStation1, String selectedStation2, String departureTime)
    {
        double travelTime = calcDist(selectedStation1, selectedStation2);

        // Check if the departureTime is null or empty
        if (departureTime == null || departureTime.isEmpty()) {
            // Handle the case where departure time is not provided
            System.err.println("Error: Departure time is not provided.");
            return null; // Or something
        }
        try {
            // Parse the departure time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime departure = LocalTime.parse(departureTime, formatter);


            departure = roundToQuarterHour(departure);
            // Calculate the estimated arrival time
            LocalTime estimatedArrivalTime = departure.plusMinutes((long) travelTime);

            // Now you can use the estimatedArrivalTime as needed
            System.out.println("Estimated Arrival Time: " + estimatedArrivalTime);
            return estimatedArrivalTime;
        } catch (DateTimeParseException e) {
            // Handle the case where departure time cannot be parsed
            System.err.println("Error: Unable to parse departure time. Please provide time in 'HH:mm' format.");
            e.printStackTrace(); //replace with more robust logging
            return null;
        }
    }
}
