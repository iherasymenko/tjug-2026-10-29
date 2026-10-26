package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.LocalDate;

@HttpExchange("https://archive-api.open-meteo.com")
public interface OpenMeteoArchiveClient {

    @GetExchange("/v1/archive?daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto")
    OpenMeteoArchiveResponse daily(@RequestParam double latitude,
                                   @RequestParam double longitude,
                                   @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   @RequestParam("temperature_unit") String temperatureUnit);

}
