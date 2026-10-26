package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.CurrentConditions;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Forecast;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.WeatherCondition;

import java.time.LocalDateTime;

public record OpenMeteoForecastResponse(Current current, OpenMeteoDaily daily) {

    public record Current(LocalDateTime time,
                          @JsonProperty("temperature_2m") double temperature,
                          @JsonProperty("relative_humidity_2m") int humidity,
                          @JsonProperty("weather_code") int weatherCode,
                          @JsonProperty("wind_speed_10m") double windSpeed) {
    }

    public Forecast toForecast() {
        CurrentConditions currentConditions = new CurrentConditions(current.time(), current.temperature(),
                current.humidity(), current.windSpeed(), WeatherCondition.fromWmoCode(current.weatherCode()));
        return new Forecast(currentConditions, daily.toDailyWeather());
    }

}
