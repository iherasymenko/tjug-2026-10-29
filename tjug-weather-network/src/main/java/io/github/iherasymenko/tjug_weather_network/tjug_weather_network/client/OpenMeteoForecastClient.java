package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("https://api.open-meteo.com")
public interface OpenMeteoForecastClient {

    @GetExchange("/v1/forecast"
            + "?current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
            + "&daily=weather_code,temperature_2m_max,temperature_2m_min"
            + "&timezone=auto")
    OpenMeteoForecastResponse forecast(@RequestParam double latitude,
                                       @RequestParam double longitude,
                                       @RequestParam("temperature_unit") String temperatureUnit,
                                       @RequestParam("wind_speed_unit") String windSpeedUnit);

}
