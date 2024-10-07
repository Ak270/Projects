package com.akshat.weather.detail;

import android.graphics.Color;

public class WeatherColorMapper {

    public static int getSkyColor(WeatherDetail weather) {
        int color;

        switch (weather.getCurrent().getCondition().getText().toLowerCase()) {
            case "clear":
                color = weather.getCurrent().getIs_day() ? Color.rgb(135, 206, 235) : Color.rgb(25, 25, 112); // Day: Sky Blue, Night: Midnight Blue
                break;
            case "cloudy":
                color = Color.rgb(176, 196, 222); // Light Steel Blue for cloudy
                break;
            case "mist":
                color = Color.rgb(169, 169, 169); // Dark Gray for mist
                break;
            case "overcast":
                color = Color.rgb(119, 136, 153); // Dark Slate Gray for overcast
                break;
            case "partly cloudy":
                color = weather.getCurrent().getIs_day() ? Color.rgb(192, 192, 192) : Color.rgb(105, 105, 105); // Day: Light Gray, Night: Dim Gray
                break;
            case "heavy rain":
                color = Color.rgb(64, 64, 64); // Dim Gray for heavy rain
                break;
            case "light rain shower":
                color = Color.rgb(100, 149, 237); // Cornflower Blue for light rain showers
                break;
            case "patchy rain nearby":
                color = Color.rgb(135, 206, 250); // Light Sky Blue for patchy rain nearby
                break;
            default:
                color = Color.rgb(135, 206, 250); // Default to light sky blue
                break;
        }

        // Adjust brightness based on cloud cover
        if (weather.getCurrent().getCloud() > 50) {
            float factor = 1 - (weather.getCurrent().getCloud() / 100.0f); // Reduce brightness based on cloud cover
            color = adjustColorBrightness(color, factor);
        }

        return color;
    }

    private static int adjustColorBrightness(int color, float factor) {
        int r = Math.max(0, Math.min(255, (int) (Color.red(color) * factor)));
        int g = Math.max(0, Math.min(255, (int) (Color.green(color) * factor)));
        int b = Math.max(0, Math.min(255, (int) (Color.blue(color) * factor)));

        return Color.rgb(r, g, b);
    }

    public static int getDarkerShade(int color, float df, float af){
        int red = (int)(Color.red(color) * df);
        int green = (int)(Color.green(color) * df);
        int blue = (int)(Color.blue(color) * df);
        int alpha = (int)(255 * af);

        return Color.argb(alpha, red, green, blue);
    }
}

