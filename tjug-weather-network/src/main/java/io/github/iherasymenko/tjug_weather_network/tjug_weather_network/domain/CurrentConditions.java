package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

import java.time.LocalDateTime;

public record CurrentConditions(LocalDateTime time, double temperature, int humidity, double windSpeed,
                                WeatherCondition condition) {
}
