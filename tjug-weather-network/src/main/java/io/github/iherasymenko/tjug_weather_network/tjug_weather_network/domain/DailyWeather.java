package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

import java.time.LocalDate;

public record DailyWeather(LocalDate date, double minTemperature, double maxTemperature, WeatherCondition condition) {
}
