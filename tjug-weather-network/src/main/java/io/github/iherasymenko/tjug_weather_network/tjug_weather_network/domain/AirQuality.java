package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

public record AirQuality(int usAqi, double pm25, double pm10) {

    public String category() {
        if (usAqi <= 50) {
            return "Good";
        }
        if (usAqi <= 100) {
            return "Moderate";
        }
        if (usAqi <= 150) {
            return "Unhealthy for Sensitive Groups";
        }
        if (usAqi <= 200) {
            return "Unhealthy";
        }
        if (usAqi <= 300) {
            return "Very Unhealthy";
        }
        return "Hazardous";
    }

}
