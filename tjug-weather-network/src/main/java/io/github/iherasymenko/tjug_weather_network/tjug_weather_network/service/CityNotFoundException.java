package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "City not found")
public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(long id) {
        super("No city with id " + id);
    }

}
