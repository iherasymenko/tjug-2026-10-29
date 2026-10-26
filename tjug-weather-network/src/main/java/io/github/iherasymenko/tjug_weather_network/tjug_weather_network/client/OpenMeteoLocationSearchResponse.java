package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.City;

import java.util.ArrayList;
import java.util.List;

public record OpenMeteoLocationSearchResponse(List<OpenMeteoLocation> results) {

    public List<City> toCities() {
        List<City> cities = new ArrayList<>();
        if (results != null) {
            for (OpenMeteoLocation location : results) {
                cities.add(location.toCity());
            }
        }
        return cities;
    }

}
