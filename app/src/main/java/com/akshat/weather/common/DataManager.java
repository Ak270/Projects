package com.akshat.weather.common;

import com.akshat.weather.detail.WeatherDetail;

import java.util.HashMap;
import java.util.Map;

public class DataManager {

    WeatherDetail weatherDetail;

    public DataManager(WeatherDetail weatherDetail){
        this.weatherDetail = weatherDetail;
    }

    public Map<String, String> getAstroDetails(){
        Map<String, String> astroDetail = new HashMap<>();
        boolean isDay = weatherDetail.getCurrent().getIs_day();
        String astroName = null;
        String currentAstroDetail = null;
        String nextAstroDetail = null;
        StringBuilder nextAstroString = new StringBuilder();

        if(isDay) {
            astroName = "SUNSET";
            currentAstroDetail = weatherDetail.getForecast().getForecastday().get(0).getAstro().getSunset();
            nextAstroString.append("Sunrise: ");
            nextAstroDetail = weatherDetail.getForecast().getForecastday().get(1).getAstro().getSunrise();
            nextAstroString.append(nextAstroDetail);
        }
        else {
            astroName = "SUNRISE";
            currentAstroDetail = weatherDetail.getForecast().getForecastday().get(0).getAstro().getSunrise();
            nextAstroString.append("Sunset: ");
            nextAstroDetail = weatherDetail.getForecast().getForecastday().get(1).getAstro().getSunset();
            nextAstroString.append(nextAstroDetail);
        }

        astroDetail.put("astroName", astroName);
        astroDetail.put("currentAstroDetail", currentAstroDetail);
        astroDetail.put("nextAstroDetail", nextAstroString.toString());

        return astroDetail;
    }

    public Map<String,String> getWindDetails(){
        Map<String, String> windDetails = new HashMap<>();
        String windTextCaps = null;
        String windSpeed = null;
        String windSpeedUnit = null;
        String windTextSmall = null;
        String gustsSpeed = null;
        String gustsSpeedUnit = null;
        String gustsTextSmall = null;
        String windDirection = null;

        windTextCaps = "WIND";
        windSpeed = String.valueOf((int)weatherDetail.getCurrent().getWind_kph());
        windSpeedUnit = "KPH";
        windTextSmall = "Wind";
        gustsSpeed = String.valueOf((int)weatherDetail.getCurrent().getGust_kph());
        gustsSpeedUnit = "KPH";
        gustsTextSmall = "Gusts";
        windDirection = weatherDetail.getCurrent().getWind_dir();

        windDetails.put("windTextCaps", windTextCaps);
        windDetails.put("windSpeed", windSpeed);
        windDetails.put("windSpeedUnit", windSpeedUnit);
        windDetails.put("windTextSmall", windTextSmall);
        windDetails.put("gustsSpeed", gustsSpeed);
        windDetails.put("gustsSpeedUnit", gustsSpeedUnit);
        windDetails.put("gustsTextSmall", gustsTextSmall);
        windDetails.put("windDirection", windDirection);

        return windDetails;
    }

    public Map<String, String> getFeelsLikeDetails(){
        Map<String,String> feelsLikeDetails = new HashMap<>();
        String feelsLikeText = null;
        String temperature = null;
        String detail = null;

        feelsLikeText = "FEELS LIKE";
        temperature = String.valueOf((int)weatherDetail.getCurrent().getFeelslike_c()) + "°";
        detail = "Humidity is making it feel warmer.";

        feelsLikeDetails.put("feelsLikeText", feelsLikeText);
        feelsLikeDetails.put("temperature", temperature);
        feelsLikeDetails.put("detail", detail);

        return feelsLikeDetails;
    }

    public Map<String, String> getHumidityDetails(){
        Map<String, String> humidityDetails = new HashMap<>();
        String humidityText = null;
        String humidityPercent = null;
        String dewPoint = null;
        StringBuilder dewPointString = new StringBuilder();

        humidityText = "HUMIDITY";
        humidityPercent = weatherDetail.getCurrent().getHumidity() + "%";
        dewPointString.append("The dew point is ");
        dewPoint = String.valueOf((int) weatherDetail.getCurrent().getDewpoint_c());
        dewPointString.append(dewPoint + "° right now.");

        humidityDetails.put("humidityText", humidityText);
        humidityDetails.put("humidityPercent", humidityPercent);
        humidityDetails.put("dewPoint", dewPointString.toString());

        return humidityDetails;
    }
}
