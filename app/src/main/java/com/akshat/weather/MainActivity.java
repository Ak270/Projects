//  API Website: WeatherApi.com

package com.akshat.weather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaCodec;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.akshat.weather.common.DataManager;
import com.akshat.weather.common.LocationDetail;
import com.akshat.weather.detail.Hour;
import com.akshat.weather.detail.WeatherColorMapper;
import com.akshat.weather.detail.WeatherDetail;
import com.akshat.weather.detail.ForecastDay;
import com.akshat.weather.fetchData.FetchDataFromServer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.ServiceWorkerWebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false; // Flag to track permission request state
    private static final String SHARED_PREFS_FILE = "AppPreferences";
    private static final String LOCATION_PERMISSION_GRANTED_KEY = "location_permission_granted";
    private static final String LOCATION_UPDATES_REQUESTED_KEY = "location_updates_requested";
    LocationDetail locationDetail;
    FetchDataFromServer fetchDataFromServer;
    DataManager dataManager;

    private void showData(String locality){
        TextView city = findViewById(R.id.city);
        city.setText(locality);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        locationDetail = new LocationDetail();
        fetchDataFromServer = new FetchDataFromServer(locationDetail);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();
        requestingLocationUpdates = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE).getBoolean(LOCATION_UPDATES_REQUESTED_KEY, false);
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if(locationResult.getLastLocation() != null) {
                handleNewLocation(locationResult.getLastLocation());
                }
            }
        };
    }

    private void checkPermissionsAndStartLocationTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!requestingLocationUpdates) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                requestingLocationUpdates = true;
                saveRequestingLocationUpdatesFlag(true);
            }
        } else {
            startLocationTracking();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestingLocationUpdates = false;
        saveRequestingLocationUpdatesFlag(false);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePermissionStatus(true);
                startLocationTracking();
            } else {
                savePermissionStatus(false);
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showSettingsDialog();
                } else {
                    Toast.makeText(this, "Permission denied. Please allow it from settings.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Permission was denied permanently. You can enable it in the app settings.")
                .setPositiveButton("Go to settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void startLocationTracking() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            Log.d("Location Tracking", "Permission not granted for location updates.");
            checkPermissionsAndStartLocationTracking();
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void handleNewLocation(Location location) {

        if(isInternetAvailable()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                locationDetail.setLatitude(latitude);
                locationDetail.setLongitude(longitude);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    locationDetail.setLocality(address.getLocality());
                    locationDetail.setCountryName(address.getCountryName());
                    locationDetail.setAddressLine(address.getAddressLine(0));

                    new FetchDataJob().execute("https://api.weatherapi.com/v1/forecast.json?key=API_PLACEHOLDER&q=QUERY_PLACEHOLDER&aqi=yes&days=10");
                }
            } catch (IOException ex) {
                Log.e("Geocoder", "Unable to get location name.", ex);
            }
        }
        else{
            TextView blank = findViewById(R.id.blank);
            blank.setText("- -");
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void savePermissionStatus(boolean isGranted){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOCATION_PERMISSION_GRANTED_KEY, isGranted);
        editor.apply();
    }

    private void saveRequestingLocationUpdatesFlag(boolean isRequesting){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOCATION_UPDATES_REQUESTED_KEY, isRequesting);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            requestingLocationUpdates = false;
            saveRequestingLocationUpdatesFlag(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!requestingLocationUpdates) {
            checkPermissionsAndStartLocationTracking();
        }
        else{
            startLocationTracking();
        }
    }

    private class FetchDataJob extends AsyncTask<String, Void, WeatherDetail> {

        @Override
        protected WeatherDetail doInBackground(String... url) {
            return fetchDataFromServer.getDataFromServer(url[0]);
        }

        public void setDetail(WeatherDetail weatherDetail){

            int backgroundColor = WeatherColorMapper.getSkyColor(weatherDetail);
            ConstraintLayout main = findViewById(R.id.main);
            main.setBackgroundColor(backgroundColor);

            TextView cl = findViewById(R.id.currentLocation);
            cl.setText("My Location");
            TextView city = findViewById(R.id.city);
            city.setText(weatherDetail.getLocation().getName());
            TextView temperature = findViewById(R.id.temperature);
            String celsius = String.valueOf(weatherDetail.getCurrent().getTemp_c());
            celsius = celsius.substring(0, 2);
            celsius += "°";
            temperature.setText(celsius);
            TextView condition = findViewById(R.id.atmosphere);
            condition.setText(weatherDetail.getCurrent().getCondition().getText());

            TextView maxiMini = findViewById(R.id.highLow);
            String temp = "H:";
            for(ForecastDay forecastDay:weatherDetail.getForecast().getForecastday()){
                temp += String.valueOf((int) forecastDay.getDay().getMaxtemp_c()) + "°  L:" + String.valueOf((int)forecastDay.getDay().getMintemp_c()) + "°";
                break;
            }
            maxiMini.setText(temp);
        }

        public void setHourDetails(WeatherDetail weatherDetail){

            LinearLayout ll = findViewById(R.id.hourlyForecastLayout);
            ll.setBackgroundResource(R.drawable.border_shadow);

            TextView[] hourCharts = createHourChart();
            ImageView[] imageViews = createHourChartImage();
            boolean startAfterCurrent = true;
            boolean nextHourWeatherCondition = true;
            int i=0;
            int k=0;

            int currentHour = getHourFromEpoch(System.currentTimeMillis());
            for(ForecastDay forecastDay: weatherDetail.getForecast().getForecastday()){
                int j=0;
                if(startAfterCurrent) {
                    for (; j < forecastDay.getHour().size(); j++) {
                        Hour hour = forecastDay.getHour().get(j);
                        int forecastHour = getHourFromEpoch(hour.getTime_epoch() * 1000);

                        if (currentHour == forecastHour) {
                            TextView hourChart = findViewById(R.id.hourChart1);
                            hourChart.setText("Now");
                            TextView timeChart = findViewById(R.id.hourChart2);
                            String forecastTemperature = String.valueOf((int) hour.getTemp_c()) + "°";
                            timeChart.setText(forecastTemperature);

                            String imageUrl = "https:"+hour.getCondition().getIcon();

                            ImageView iv = findViewById(R.id.hourChartImg1);
                            Picasso.get()
                                    .load(imageUrl)
                                    .resize(50, 70) // Resize to 100x100 pixels
                                    .centerCrop() // Optional: crop to fill the ImageView
                                    .into(iv);
                            break;
                        }
                    }
                    j++;
                    startAfterCurrent = false;
                }


                for(; j<forecastDay.getHour().size(); j++){
                    if(i>=46) break;
                    Hour hour = forecastDay.getHour().get(j);
                    TextView hourChart = hourCharts[i++];
                    String imageUrl = "https:"+hour.getCondition().getIcon();

                    ImageView iv = imageViews[k++];
                    Picasso.get()
                            .load(imageUrl)
                            .resize(50, 70) // Resize to 100x100 pixels
                            .centerCrop() // Optional: crop to fill the ImageView
                            .into(iv);

                    int forecastHour = getHourFromEpoch(hour.getTime_epoch()*1000);
                    String AM_PM = "AM";
                    if(forecastHour >= 12){
                        forecastHour -= 12;
                        AM_PM = "PM";
                    }
                    if(forecastHour == 0){
                        forecastHour = 12;
                    }

                    String hourText = String.valueOf(forecastHour)+AM_PM;
                    hourChart.setText(hourText);
                    if(nextHourWeatherCondition){
                        TextView nextHour = findViewById(R.id.hourChart);
                        nextHour.setText(hour.getCondition().getText()+" expected around "+ hourText);
                        nextHourWeatherCondition = false;
                    }

                    TextView timeChart = hourCharts[i++];
                    String forecastTemperature = String.valueOf((int)hour.getTemp_c()) + "°" ;
                    timeChart.setText(forecastTemperature);
                }

            }
        }

        public void setForecastDetail(WeatherDetail weatherDetail){
            LinearLayout ll = findViewById(R.id.dailyForecastLayout);
            ll.setBackgroundResource(R.drawable.border_shadow);

            TextView forecast = findViewById(R.id.forecast);
            forecast.setText("10-DAY FORECAST");
            int i=0;
            TextView[] daysView  = createDayView();
            ImageView[] imgView = createImgView();
            TextView[] daysCondition = createConditionView();
            TextView[] daysTemperature = createTemperatureView();
            for(ForecastDay forecastDay: weatherDetail.getForecast().getForecastday()){
                Instant instant = Instant.ofEpochSecond(forecastDay.getDateEpoch());
                ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                LocalDate localDate = zonedDateTime.toLocalDate();
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                if(i>0){
                    TextView day = daysView[i];
                    String s = String.valueOf(dayOfWeek);
                    s = s.substring(0, 3);
                    day.setText(s);
                }
                else{
                    TextView day = daysView[i];
                    day.setText("Today");
                }

                String imageUrl = "https:"+forecastDay.getDay().getCondition().getIcon();

                ImageView img = imgView[i];
                Picasso.get()
                        .load(imageUrl)
                        .resize(50, 70) // Resize to 100x100 pixels
                        .centerCrop() // Optional: crop to fill the ImageView
                        .into(img);

                TextView condition = daysCondition[i];
                condition.setText(forecastDay.getDay().getCondition().getText());

                TextView temperature = daysTemperature[i++];
                String temp = String.valueOf((int)forecastDay.getDay().getMintemp_c()) + "° /" + String.valueOf((int)forecastDay.getDay().getMaxtemp_c()) + "°";
                temperature.setText(temp);
            }
        }

        public void setAirQuality(WeatherDetail weatherDetail){
            LinearLayout ll = findViewById(R.id.airQualityLayout);
            ll.setBackgroundResource(R.drawable.border_shadow);

            TextView aq = findViewById(R.id.airQuality);
            aq.setText("AIR QUALITY");
            TextView aqi = findViewById(R.id.airQualityIndex);
            weatherDetail.getCurrent().getAirQuality().generateAQI();
            aqi.setText(String.valueOf(weatherDetail.getCurrent().getAirQuality().getMaxAQI()));

            TextView airQualityStatus = findViewById(R.id.airQualityStatus);
            airQualityStatus.setText(weatherDetail.getCurrent().getAirQuality().getAqiMessage());
        }

        public void setUvIndex(WeatherDetail weatherDetail){
            LinearLayout ll = findViewById(R.id.uvIndexLayout);
            ll.setBackgroundResource(R.drawable.border_shadow);

            TextView uvText = findViewById(R.id.uvIndexText);
            uvText.setText("UV INDEX");

            TextView uvValue = findViewById(R.id.uvIndexValue);
            uvValue.setText(String.valueOf((int)weatherDetail.getCurrent().getUv()));
        }

        public void setAstroDetail(WeatherDetail weatherDetail){
            LinearLayout ll = findViewById(R.id.astroLayout);
            ll.setBackgroundResource(R.drawable.border_shadow);

            TextView astroNameTextView = findViewById(R.id.astroName);
            TextView currentAstroDetailTextView = findViewById(R.id.currentAstroDetail);
            TextView nextAstroDetailTextView = findViewById(R.id.nextAstroDetail);
            Map<String,String> astroDetail = dataManager.getAstroDetails();

            astroNameTextView.setText(astroDetail.get("astroName"));
            currentAstroDetailTextView.setText(astroDetail.get("currentAstroDetail"));
            nextAstroDetailTextView.setText(astroDetail.get("nextAstroDetail"));
        }

        public void setWindDetails(WeatherDetail weatherDetail){
            LinearLayout windLayout = findViewById(R.id.windDetailsLayout);
            windLayout.setBackgroundResource(R.drawable.border_shadow);

            TextView windTextView = findViewById(R.id.windTextCaps);
            TextView windSpeedTextView = findViewById(R.id.windSpeed);
            TextView windSpeedUnitTextView = findViewById(R.id.windSpeedUnit);
            TextView windSmallTextView = findViewById(R.id.windTextSmall);
            TextView gustsSpeedTextView = findViewById(R.id.gustsSpeed);
            TextView gustsSpeedUnitTextView = findViewById(R.id.gustsSpeedUnit);
            TextView gustsSmallTextView = findViewById(R.id.gustsTextSmall);
            TextView windDirectionTextView = findViewById(R.id.windDirection);
            Map<String, String> windDetails = dataManager.getWindDetails();

            windTextView.setText(windDetails.get("windTextCaps"));
            windSpeedTextView.setText(windDetails.get("windSpeed"));
            windSmallTextView.setText(windDetails.get("windTextSmall"));
            windSpeedUnitTextView.setText(windDetails.get("windSpeedUnit"));
            gustsSpeedTextView.setText(windDetails.get("gustsSpeed"));
            gustsSpeedUnitTextView.setText(windDetails.get("gustsSpeedUnit"));
            gustsSmallTextView.setText(windDetails.get("gustsTextSmall"));
            windDirectionTextView.setText(windDetails.get("windDirection"));
        }

        public void setTemperatureFeelsLike(WeatherDetail weatherDetail){
            LinearLayout feelsLike = findViewById(R.id.feelsLikeLayout);
            feelsLike.setBackgroundResource(R.drawable.border_shadow);

            TextView feelsLikeTextView = findViewById(R.id.feelsLikeText);
            TextView temperatureFeelsLikeTextView = findViewById(R.id.temperatureFeelsLike);
            TextView feelsLikeDueToTextView = findViewById(R.id.feelsLikedueTo);
            Map<String, String> feelsLikeDetails = dataManager.getFeelsLikeDetails();

            feelsLikeTextView.setText(feelsLikeDetails.get("feelsLikeText"));
            temperatureFeelsLikeTextView.setText(feelsLikeDetails.get("temperature"));
            feelsLikeDueToTextView.setText(feelsLikeDetails.get("detail"));
        }

        public void setHumidityDetails(WeatherDetail weatherDetail){
            LinearLayout humidityLayout = findViewById(R.id.humidityLayout);
            humidityLayout.setBackgroundResource(R.drawable.border_shadow);

            TextView humidityTextView = findViewById(R.id.humidityText);
            TextView humidityPercentTextView = findViewById(R.id.humidityPercent);
            TextView dewPointTextView = findViewById(R.id.dewPoint);
            Map<String, String> humidityDetails = dataManager.getHumidityDetails();

            humidityTextView.setText(humidityDetails.get("humidityText"));
            humidityPercentTextView.setText(humidityDetails.get("humidityPercent"));
            dewPointTextView.setText(humidityDetails.get("dewPoint"));
        }

        public void setReportDetails(){
            LinearLayout reportLayout = findViewById(R.id.reportLayout);
            reportLayout.setBackgroundResource(R.drawable.border_shadow);

            TextView reportTextView = findViewById(R.id.reportTextView);
            TextView reportDescription = findViewById(R.id.reportDescription);

            reportTextView.setText("Report an Issue");
            reportDescription.setText("You can describe the current conditions at your location to help improve forecasts.");
        }

        @Override
        protected void onPostExecute(WeatherDetail weatherDetail){

            FrameLayout progressOverlay = findViewById(R.id.progressOverlay);
            progressOverlay.setVisibility(View.GONE);

            dataManager = new DataManager(weatherDetail);

            setDetail(weatherDetail);
            setForecastDetail(weatherDetail);
            setHourDetails(weatherDetail);
            setAirQuality(weatherDetail);
            setUvIndex(weatherDetail);
            setAstroDetail(weatherDetail);
            setWindDetails(weatherDetail);
            setTemperatureFeelsLike(weatherDetail);
            setHumidityDetails(weatherDetail);
            setReportDetails();
        }
    }

    private static int getHourFromEpoch(long epochTime) {

        Instant instant = Instant.ofEpochMilli(epochTime);
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.getHour();
    }

    private TextView[] createDayView() {
        return new TextView[]{
                findViewById(R.id.day1),
                findViewById(R.id.day2),
                findViewById(R.id.day3),
                findViewById(R.id.day4),
                findViewById(R.id.day5),
                findViewById(R.id.day6),
                findViewById(R.id.day7),
                findViewById(R.id.day8),
                findViewById(R.id.day9),
                findViewById(R.id.day10)
        };
    }

    private ImageView[] createImgView(){
        return new ImageView[]{
                findViewById(R.id.atmDay1),
                findViewById(R.id.atmDay2),
                findViewById(R.id.atmDay3),
                findViewById(R.id.atmDay4),
                findViewById(R.id.atmDay5),
                findViewById(R.id.atmDay6),
                findViewById(R.id.atmDay7),
                findViewById(R.id.atmDay8),
                findViewById(R.id.atmDay9),
                findViewById(R.id.atmDay10),
        };
    }

    private TextView[] createConditionView(){
        return new TextView[]{
                findViewById(R.id.conditionDay1),
                findViewById(R.id.conditionDay2),
                findViewById(R.id.conditionDay3),
                findViewById(R.id.conditionDay4),
                findViewById(R.id.conditionDay5),
                findViewById(R.id.conditionDay6),
                findViewById(R.id.conditionDay7),
                findViewById(R.id.conditionDay8),
                findViewById(R.id.conditionDay9),
                findViewById(R.id.conditionDay10)
        };
    }

    private TextView[] createTemperatureView(){
        return new TextView[]{
                findViewById(R.id.temperatureDay1),
                findViewById(R.id.temperatureDay2),
                findViewById(R.id.temperatureDay3),
                findViewById(R.id.temperatureDay4),
                findViewById(R.id.temperatureDay5),
                findViewById(R.id.temperatureDay6),
                findViewById(R.id.temperatureDay7),
                findViewById(R.id.temperatureDay8),
                findViewById(R.id.temperatureDay9),
                findViewById(R.id.temperatureDay10),
        };
    }

    private TextView[] createHourChart(){
        return new TextView[]{
                findViewById(R.id.hourChart3),
                findViewById(R.id.hourChart4),
                findViewById(R.id.hourChart5),
                findViewById(R.id.hourChart6),
                findViewById(R.id.hourChart7),
                findViewById(R.id.hourChart8),
                findViewById(R.id.hourChart9),
                findViewById(R.id.hourChart10),
                findViewById(R.id.hourChart11),
                findViewById(R.id.hourChart12),
                findViewById(R.id.hourChart13),
                findViewById(R.id.hourChart14),
                findViewById(R.id.hourChart15),
                findViewById(R.id.hourChart16),
                findViewById(R.id.hourChart17),
                findViewById(R.id.hourChart18),
                findViewById(R.id.hourChart19),
                findViewById(R.id.hourChart20),
                findViewById(R.id.hourChart21),
                findViewById(R.id.hourChart22),
                findViewById(R.id.hourChart23),
                findViewById(R.id.hourChart24),
                findViewById(R.id.hourChart25),
                findViewById(R.id.hourChart26),
                findViewById(R.id.hourChart27),
                findViewById(R.id.hourChart28),
                findViewById(R.id.hourChart29),
                findViewById(R.id.hourChart30),
                findViewById(R.id.hourChart31),
                findViewById(R.id.hourChart32),
                findViewById(R.id.hourChart33),
                findViewById(R.id.hourChart34),
                findViewById(R.id.hourChart35),
                findViewById(R.id.hourChart36),
                findViewById(R.id.hourChart37),
                findViewById(R.id.hourChart38),
                findViewById(R.id.hourChart39),
                findViewById(R.id.hourChart40),
                findViewById(R.id.hourChart41),
                findViewById(R.id.hourChart42),
                findViewById(R.id.hourChart43),
                findViewById(R.id.hourChart44),
                findViewById(R.id.hourChart45),
                findViewById(R.id.hourChart46),
                findViewById(R.id.hourChart47),
                findViewById(R.id.hourChart48)
        };
    }

    private ImageView[] createHourChartImage(){
        return new ImageView[]{
                findViewById(R.id.hourChartImg2),
                findViewById(R.id.hourChartImg3),
                findViewById(R.id.hourChartImg4),
                findViewById(R.id.hourChartImg5),
                findViewById(R.id.hourChartImg6),
                findViewById(R.id.hourChartImg7),
                findViewById(R.id.hourChartImg8),
                findViewById(R.id.hourChartImg9),
                findViewById(R.id.hourChartImg10),
                findViewById(R.id.hourChartImg11),
                findViewById(R.id.hourChartImg12),
                findViewById(R.id.hourChartImg13),
                findViewById(R.id.hourChartImg14),
                findViewById(R.id.hourChartImg15),
                findViewById(R.id.hourChartImg16),
                findViewById(R.id.hourChartImg17),
                findViewById(R.id.hourChartImg18),
                findViewById(R.id.hourChartImg19),
                findViewById(R.id.hourChartImg20),
                findViewById(R.id.hourChartImg21),
                findViewById(R.id.hourChartImg22),
                findViewById(R.id.hourChartImg23),
                findViewById(R.id.hourChartImg24)
        };
    }
}