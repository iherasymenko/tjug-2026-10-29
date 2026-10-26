package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

public enum Units {

    METRIC("celsius", "kmh", "°C", "km/h"),
    IMPERIAL("fahrenheit", "mph", "°F", "mph");

    private final String openMeteoTemperatureUnit;
    private final String openMeteoWindSpeedUnit;
    private final String temperatureSymbol;
    private final String windSpeedSymbol;

    Units(String openMeteoTemperatureUnit, String openMeteoWindSpeedUnit, String temperatureSymbol, String windSpeedSymbol) {
        this.openMeteoTemperatureUnit = openMeteoTemperatureUnit;
        this.openMeteoWindSpeedUnit = openMeteoWindSpeedUnit;
        this.temperatureSymbol = temperatureSymbol;
        this.windSpeedSymbol = windSpeedSymbol;
    }

    public static Units fromCookieValue(String value) {
        return "imperial".equalsIgnoreCase(value) ? IMPERIAL : METRIC;
    }

    public String cookieValue() {
        return name().toLowerCase();
    }

    public String openMeteoTemperatureUnit() {
        return openMeteoTemperatureUnit;
    }

    public String openMeteoWindSpeedUnit() {
        return openMeteoWindSpeedUnit;
    }

    public String temperatureSymbol() {
        return temperatureSymbol;
    }

    public String windSpeedSymbol() {
        return windSpeedSymbol;
    }

}
