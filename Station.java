package com.example.ov;
import java.time.LocalTime;

public class Station {
    //hold transportlines departuretimes and names of stations
    private final String name;
    private final LocalTime arrivalTime;

    public Station(String name, LocalTime ArrivalTime)
    {
        this.name = name;
        this.arrivalTime = ArrivalTime;
    }

    public String getStationName()
    {
        return name;
    }
}

