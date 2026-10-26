package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.GuestbookEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class Guestbook {

    static final int MAX_ENTRIES = 50;

    private final Deque<GuestbookEntry> entries = new ArrayDeque<>();

    public synchronized void sign(String name, String message) {
        entries.addFirst(new GuestbookEntry(name, message, LocalDateTime.now()));
        while (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }
    }

    public synchronized List<GuestbookEntry> entries() {
        return List.copyOf(entries);
    }

}
