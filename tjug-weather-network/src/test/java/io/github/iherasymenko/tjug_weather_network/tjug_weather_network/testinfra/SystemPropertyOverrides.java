package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

final class SystemPropertyOverrides {

    private final Map<String, String> properties = new LinkedHashMap<>();
    private final Map<String, String> previousValues = new HashMap<>();

    SystemPropertyOverrides add(String key, String value) {
        properties.put(key, value);
        return this;
    }

    void override() {
        for (var property : properties.entrySet()) {
            previousValues.put(property.getKey(), System.setProperty(property.getKey(), property.getValue()));
        }
    }

    void rollback() {
        for (var previous : previousValues.entrySet()) {
            if (previous.getValue() == null) {
                System.clearProperty(previous.getKey());
            } else {
                System.setProperty(previous.getKey(), previous.getValue());
            }
        }
        previousValues.clear();
    }

}
