package com.example.ov;

import java.util.*;
public class TransportLine
{
    private final String name;
    private final List<Station> stations;
    private final Map<String, Integer> travelTimesMap = new HashMap<>();

    public TransportLine(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
    }

    String getName() // later voor welke transportline je wilt
    {
        return name;
    }

    public List<Station> getStationObject()
    {
        return stations;
    }
    public List<String> getStations()
    {
        List<String> stationNames = new ArrayList<>();
        for (Station station : stations) {
            stationNames.add(station.getStationName());
        }
        return stationNames;
    }

    public void addStation(Station station) // miss andere tijd format
    {
        stations.add(station);
    }

    @Override
    public String toString() {
        return name;
    }
}
