package com.akshat.weather.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Condition {
    private String text;
    private String icon;
    private int code;

    // Default constructor
    public Condition() {}

    // Getters and setters
    @JsonProperty("text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("icon")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
