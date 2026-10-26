package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import java.util.ArrayList;
import java.util.List;

final class MyCities {

    static final String COOKIE_NAME = "mycities";
    static final int MAX_CITIES = 10;

    private final List<Long> ids;

    private MyCities(List<Long> ids) {
        this.ids = ids;
    }

    static MyCities fromCookieValue(String value) {
        List<Long> ids = new ArrayList<>();
        if (value != null) {
            for (String id : value.split("\\|")) {
                try {
                    long parsed = Long.parseLong(id);
                    if (!ids.contains(parsed) && ids.size() < MAX_CITIES) {
                        ids.add(parsed);
                    }
                } catch (NumberFormatException e) {
                    // Not a city id
                }
            }
        }
        return new MyCities(ids);
    }

    List<Long> ids() {
        return List.copyOf(ids);
    }

    boolean contains(long id) {
        return ids.contains(id);
    }

    MyCities add(long id) {
        List<Long> updated = new ArrayList<>(ids);
        updated.remove(id);
        updated.add(id);
        while (updated.size() > MAX_CITIES) {
            updated.removeFirst();
        }
        return new MyCities(updated);
    }

    MyCities remove(long id) {
        List<Long> updated = new ArrayList<>(ids);
        updated.remove(id);
        return new MyCities(updated);
    }

    String cookieValue() {
        StringBuilder value = new StringBuilder();
        for (Long id : ids) {
            if (!value.isEmpty()) {
                value.append('|');
            }
            value.append(id);
        }
        return value.toString();
    }

}
