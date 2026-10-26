package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

public record City(long id, String name, String region, String country, String countryCode,
                   double latitude, double longitude) {

    public String displayName() {
        StringBuilder displayName = new StringBuilder(name);
        if (region != null && !region.isBlank()) {
            displayName.append(", ").append(region);
        }
        if (country != null && !country.isBlank()) {
            displayName.append(", ").append(country);
        }
        return displayName.toString();
    }

}
