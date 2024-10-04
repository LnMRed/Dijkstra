package com.example.ov;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Timetable {
    private final Map<String, Map<String, String>> timetableMap;
    //why did it not commit
    LocalTime startTime = LocalTime.of(7,0);
    LocalTime endTime  = LocalTime.of(23,0);
    public Timetable()
    {
        this.timetableMap = new HashMap<>();
    }

    public void makeTrainsRun(TransportLine line) {
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime) || currentTime.equals(endTime)) {
            for (Station station : line.getStationObject()) {
                addTimetableEntry(station.getStationName(), currentTime.toString(), line.getName());
            }
            currentTime = currentTime.plusMinutes(15);
        }
    }

    public void addTimetableEntry(String station, String time, String line) {
        timetableMap.computeIfAbsent(station, k -> new HashMap<>()).put(time, line);
    }
}
