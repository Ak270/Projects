package com.akshat.weather.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class AirQuality {
    @JsonProperty("co")
    private double co;

    @JsonProperty("no2")
    private double no2;

    @JsonProperty("o3")
    private double o3;

    @JsonProperty("so2")
    private double so2;

    @JsonProperty("pm2_5")
    private double pm2_5;

    @JsonProperty("pm10")
    private double pm10;

    @JsonProperty("us-epa-index")
    private int us_epa_index;

    @JsonProperty("gb-defra-index")
    private int gb_defra_index;

    @JsonProperty("aqi_data")
    private String aqi_data;

    private int maxAQI;

    private String aqiMessage;

    private static final double[] PM25_BREAKPOINTS = {0.0, 12.0, 35.5, 55.5, 150.5, 250.5, 500.4};
    private static final int[] PM25_AQI_VALUES = {0, 50, 100, 150, 200, 300, 500};

    private static final double[] PM10_BREAKPOINTS = {0.0, 54.0, 154.0, 254.0, 354.0, 424.0, 604.0};
    private static final int[] PM10_AQI_VALUES = {0, 50, 100, 150, 200, 300, 500};


    public AirQuality() {
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public double getCo() {
        return co;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public double getNo2() {
        return no2;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public double getO3() {
        return o3;
    }

    public void setO3(double o3) {
        this.o3 = o3;
    }

    public double getSo2() {
        return so2;
    }

    public void setSo2(double so2) {
        this.so2 = so2;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public int getUs_epa_index() {
        return us_epa_index;
    }

    public void setUs_epa_index(int us_epa_index) {
        this.us_epa_index = us_epa_index;
    }

    public int getGb_defra_index() {
        return gb_defra_index;
    }

    public void setGb_defra_index(int gb_defra_index) {
        this.gb_defra_index = gb_defra_index;
    }

    public String getAqi_data() {
        return aqi_data;
    }

    public void setAqi_data(String aqi_data) {
        this.aqi_data = aqi_data;
    }

    public int getMaxAQI() {
        return maxAQI;
    }

    public String getAqiMessage() {
        return aqiMessage;
    }

    public void generateAQI() {
        // Calculate AQI for each pollutant
        int aqiCo = calculateAqiCo(co);
        int aqiNo2 = calculateAqiNo2(no2);
        int aqiO3 = calculateAqiO3(o3);
        int aqiSo2 = calculateAqiSo2(so2);
        int aqiPm2_5 = calculateAqiPm2_5(pm2_5);
        int aqiPm10 = calculateAqiPm10(pm10);

        // Determine the overall AQI
        maxAQI = Math.max(Math.max(Math.max(Math.max(aqiCo, aqiNo2), aqiO3), aqiSo2), Math.max(aqiPm2_5, aqiPm10));

        aqiMessage = getAqiMessage(maxAQI);

    }

    // Calculate AQI for Carbon Monoxide (CO)
    public static int calculateAqiCo(double co) {
        if (co <= 4.4) return 0; // Good
        if (co <= 9.4) return 51; // Moderate
        if (co <= 12.4) return 101; // Unhealthy for Sensitive Groups
        return 151; // Unhealthy
    }

    // Calculate AQI for Nitrogen Dioxide (NO2)
    public static int calculateAqiNo2(double no2) {
        if (no2 <= 53) return 0; // Good
        if (no2 <= 100) return 51; // Moderate
        if (no2 <= 360) return 101; // Unhealthy for Sensitive Groups
        return 151; // Unhealthy
    }

    // Calculate AQI for Ozone (O3)
    public static int calculateAqiO3(double o3) {
        if (o3 <= 180) return 0; // Good
        if (o3 <= 240) return 51; // Moderate
        if (o3 <= 400) return 101; // Unhealthy for Sensitive Groups
        return 151; // Unhealthy
    }

    // Calculate AQI for Sulfur Dioxide (SO2)
    public static int calculateAqiSo2(double so2) {
        if (so2 <= 40) return 0; // Good
        if (so2 <= 80) return 51; // Moderate
        if (so2 <= 380) return 101; // Unhealthy for Sensitive Groups
        return 151; // Unhealthy
    }

    // Calculate AQI for Particulate Matter 2.5 (PM2.5)
    public static int calculateAqiPm2_5(double pm2_5) {
        if (pm2_5 <= 12) return 0; // Good
        if (pm2_5 <= 35) return 51; // Moderate
        if (pm2_5 <= 55) return 101; // Unhealthy for Sensitive Groups
        return 151; // Unhealthy
    }

    // Calculate AQI for Particulate Matter 10 (PM10)
    public static int calculateAqiPm10(double pm10) {
        if (pm10 <= 54) return 0; // Good
        if (pm10 <= 154) return 51; // Moderate
        if (pm10 <= 254) return 101; // Unhealthy for Sensitive Groups
        return 151; // Unhealthy
    }

    // Get AQI message based on AQI value
    public static String getAqiMessage(int aqi) {
        if (aqi <= 50) return "Good: Air quality is considered satisfactory and air pollution poses little or no risk.";
        if (aqi <= 100) return "Moderate: Air quality is acceptable; however, some pollutants may be a concern for a small number of people.";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups: Members of sensitive groups may experience health effects; the general public is not likely to be affected.";
        if (aqi <= 200) return "Unhealthy: Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects.";
        if (aqi <= 300) return "Very Unhealthy: Health alert; everyone may experience more serious health effects.";
        return "Hazardous: Health warnings of emergency conditions; the entire population is more likely to be affected.";
    }

}


