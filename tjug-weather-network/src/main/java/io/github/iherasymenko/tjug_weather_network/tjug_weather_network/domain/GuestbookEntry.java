package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain;

import java.time.LocalDateTime;

public record GuestbookEntry(String name, String message, LocalDateTime signedAt) {
}
