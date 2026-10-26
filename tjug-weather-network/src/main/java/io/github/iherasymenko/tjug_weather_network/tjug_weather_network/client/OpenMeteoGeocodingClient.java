package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("https://geocoding-api.open-meteo.com")
public interface OpenMeteoGeocodingClient {

    @GetExchange("/v1/search?count=10&language=en&format=json")
    OpenMeteoLocationSearchResponse search(@RequestParam String name);

    @GetExchange("/v1/get")
    OpenMeteoLocation get(@RequestParam long id);

}
