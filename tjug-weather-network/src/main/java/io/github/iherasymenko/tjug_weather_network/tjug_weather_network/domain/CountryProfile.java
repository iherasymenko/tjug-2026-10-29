package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

import java.util.List;

public record CountryProfile(String name, String capital, String currencyCode, String phoneCode, String flagUrl,
                             List<String> languages) {
}
