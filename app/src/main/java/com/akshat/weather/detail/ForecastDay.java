package com.akshat.weather.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ForecastDay {
    @JsonProperty("date")
    private String date;

    @JsonProperty("date_epoch")
    private long date_epoch;

    @JsonProperty("day")
    private Day day;

    @JsonProperty("astro")
    private Astro astro;

    @JsonProperty("hour")
    private List<Hour> hour;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateEpoch() {
        return date_epoch;
    }

    public void setDateEpoch(long date_epoch) {
        this.date_epoch = date_epoch;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Astro getAstro() {
        return astro;
    }

    public void setAstro(Astro astro) {
        this.astro = astro;
    }

    public List<Hour> getHour() {
        return hour;
    }

    public void setHour(List<Hour> hour) {
        this.hour = hour;
    }
}
