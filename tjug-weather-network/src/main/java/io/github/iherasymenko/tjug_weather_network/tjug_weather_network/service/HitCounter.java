package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class HitCounter {

    private final AtomicLong hits = new AtomicLong();

    public long hit() {
        return hits.incrementAndGet();
    }

}
