package com.akshat.weather.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Astro {
    @JsonProperty("sunrise")
    private String sunrise;

    @JsonProperty("sunset")
    private String sunset;

    @JsonProperty("moonrise")
    private String moonrise;

    @JsonProperty("moonset")
    private String moonset;

    @JsonProperty("moon_phase")
    private String moon_phase;

    @JsonProperty("moon_illumination")
    private int moon_illumination;

    @JsonProperty("is_moon_up")
    private int is_moon_up;

    @JsonProperty("is_sun_up")
    private int is_sun_up;

    public int getIs_moon_up() {
        return is_moon_up;
    }

    public void setIs_moon_up(int is_moon_up) {
        this.is_moon_up = is_moon_up;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public String getMoon_phase() {
        return moon_phase;
    }

    public void setMoon_phase(String moon_phase) {
        this.moon_phase = moon_phase;
    }

    public int getMoon_illumination() {
        return moon_illumination;
    }

    public void setMoon_illumination(int moon_illumination) {
        this.moon_illumination = moon_illumination;
    }

    public int getIs_sun_up() {
        return is_sun_up;
    }

    public void setIs_sun_up(int is_sun_up) {
        this.is_sun_up = is_sun_up;
    }
}
