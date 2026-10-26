package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.City;

public record OpenMeteoLocation(long id, String name, @JsonProperty("admin1") String region, String country,
                                @JsonProperty("country_code") String countryCode, double latitude, double longitude) {

    public City toCity() {
        return new City(id, name, region, country, countryCode, latitude, longitude);
    }

}
