package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("https://air-quality-api.open-meteo.com")
public interface OpenMeteoAirQualityClient {

    @GetExchange("/v1/air-quality?current=us_aqi,pm2_5,pm10&timezone=auto")
    OpenMeteoAirQualityResponse current(@RequestParam double latitude, @RequestParam double longitude);

}
