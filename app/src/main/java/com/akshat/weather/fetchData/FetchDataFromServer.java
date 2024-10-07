package com.akshat.weather.fetchData;

import android.util.Log;

import androidx.annotation.Nullable;

import com.akshat.weather.common.LocationDetail;
import com.akshat.weather.detail.WeatherDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchDataFromServer {

    LocationDetail locationDetail;
    private final OkHttpClient client;
    private ObjectMapper objectMapper;
    String APIKey = "d1ae48f4dae24c62a7a134517242709";

    public FetchDataFromServer(LocationDetail locationDetail) {
        this.locationDetail = locationDetail;
        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    public WeatherDetail getDataFromServer(String url) {

        String queryParameter = locationDetail.getLatitude() +  ", " + locationDetail.getLongitude();
        url = url.replace("API_PLACEHOLDER", APIKey);
        url = url.replace("QUERY_PLACEHOLDER", queryParameter);

        //get response from server
        String response = getResponse(url);

        //parse response
        return parseResponse(response);
    }

    private WeatherDetail parseResponse(String response) {
        WeatherDetail weatherDetail = null;
        try {
            weatherDetail = objectMapper.readValue(response, WeatherDetail.class);
        } catch (JsonProcessingException ex) {
            Log.e("Error: ", "Failed to Parse Json object. Exception: "+ex);
        }
        return weatherDetail;
    }

    private @Nullable String getResponse(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if(response.isSuccessful() && response.body() != null){
                return response.body().string();
            }
            else{
                return "fail";
            }
        }
        catch(IOException ex){
            Log.e("error: ", "Failed to get response. Exception: ", ex);
            return null;
        }
    }

}
