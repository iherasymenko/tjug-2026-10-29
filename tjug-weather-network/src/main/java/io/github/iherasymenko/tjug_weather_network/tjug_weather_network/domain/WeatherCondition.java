package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

public enum WeatherCondition {

    CLEAR("Clear", "clear.gif"),
    PARTLY_CLOUDY("Partly cloudy", "partly-cloudy.gif"),
    OVERCAST("Overcast", "cloudy.gif"),
    FOG("Fog", "fog.gif"),
    DRIZZLE("Drizzle", "rain.gif"),
    RAIN("Rain", "rain.gif"),
    SNOW("Snow", "snow.gif"),
    THUNDERSTORM("Thunderstorm", "thunderstorm.gif"),
    UNKNOWN("Unknown", "cloudy.gif");

    private final String description;
    private final String icon;

    WeatherCondition(String description, String icon) {
        this.description = description;
        this.icon = icon;
    }

    public static WeatherCondition fromWmoCode(int code) {
        return switch (code) {
            case 0, 1 -> CLEAR;
            case 2 -> PARTLY_CLOUDY;
            case 3 -> OVERCAST;
            case 45, 48 -> FOG;
            case 51, 53, 55, 56, 57 -> DRIZZLE;
            case 61, 63, 65, 66, 67, 80, 81, 82 -> RAIN;
            case 71, 73, 75, 77, 85, 86 -> SNOW;
            case 95, 96, 99 -> THUNDERSTORM;
            default -> UNKNOWN;
        };
    }

    public String description() {
        return description;
    }

    public String icon() {
        return icon;
    }

}
