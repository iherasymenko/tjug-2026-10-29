package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.DailyWeather;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.WeatherCondition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record OpenMeteoDaily(List<LocalDate> time,
                             @JsonProperty("weather_code") List<Integer> weatherCode,
                             @JsonProperty("temperature_2m_max") List<Double> maxTemperature,
                             @JsonProperty("temperature_2m_min") List<Double> minTemperature) {

    public List<DailyWeather> toDailyWeather() {
        List<DailyWeather> days = new ArrayList<>();
        for (int i = 0; i < time.size(); i++) {
            days.add(new DailyWeather(time.get(i), minTemperature.get(i), maxTemperature.get(i),
                    WeatherCondition.fromWmoCode(weatherCode.get(i))));
        }
        return days;
    }

}
