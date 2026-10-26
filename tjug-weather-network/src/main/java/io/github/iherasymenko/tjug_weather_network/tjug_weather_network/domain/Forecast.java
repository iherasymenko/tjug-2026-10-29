package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

import java.util.List;

public record Forecast(CurrentConditions current, List<DailyWeather> days) {
}
