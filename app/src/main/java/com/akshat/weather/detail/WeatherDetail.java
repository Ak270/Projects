package com.akshat.weather.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherDetail {
    private Location location;
    private Current current;
    private Forecast forecast;

    // Default constructor
    public WeatherDetail() {}

    // Getters and setters
    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("current")
    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    @JsonProperty("forecast")
    public Forecast getForecast(){
        return forecast;
    }

    public void setForecast(Forecast forecast){
        this.forecast = forecast;
    }
}
