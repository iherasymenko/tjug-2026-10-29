package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockRequest;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockResponse;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockVirtualHost;

public final class OpenMeteoArchiveApi extends MockVirtualHost {

    public OpenMeteoArchiveApi() {
        get("/v1/archive", this::archive);
    }

    @Override
    protected String host() {
        return "archive-api.open-meteo.com";
    }

    private MockResponse archive(MockRequest request) {
        if (!"weather_code,temperature_2m_max,temperature_2m_min".equals(request.queryParam("daily"))
                || !"auto".equals(request.queryParam("timezone"))
                || request.queryParam("start_date") == null
                || !request.queryParam("start_date").equals(request.queryParam("end_date"))) {
            return MockResponse.notFound();
        }

        double latitude;
        double longitude;
        try {
            latitude = Double.parseDouble(request.queryParam("latitude"));
            longitude = Double.parseDouble(request.queryParam("longitude"));
        } catch (NullPointerException | NumberFormatException e) {
            return MockResponse.notFound();
        }
        String day = request.queryParam("start_date");
        String unit = request.queryParam("temperature_unit");

        // Toronto, Ontario, a year before the forecast's current day, in metric units: a clear, chilly day
        if (latitude == 43.70643 && longitude == -79.39864 && day.equals("2025-10-26") && "celsius".equals(unit)) {
            return MockResponse.json("""
                    {
                      "latitude": 43.690685, "longitude": -79.41174, "generationtime_ms": 0.15,
                      "utc_offset_seconds": -14400, "timezone": "America/Toronto", "timezone_abbreviation": "GMT-4", "elevation": 165.0,
                      "daily_units": {"time": "iso8601", "weather_code": "wmo code", "temperature_2m_max": "°C", "temperature_2m_min": "°C"},
                      "daily": {"time": ["2025-10-26"], "weather_code": [0], "temperature_2m_max": [8.2], "temperature_2m_min": [0.3]}
                    }
                    """);
        }
        if (latitude == 43.70643 && longitude == -79.39864 && day.equals("2025-10-26") && "fahrenheit".equals(unit)) {
            return MockResponse.json("""
                    {
                      "latitude": 43.690685, "longitude": -79.41174, "generationtime_ms": 0.15,
                      "utc_offset_seconds": -14400, "timezone": "America/Toronto", "timezone_abbreviation": "GMT-4", "elevation": 165.0,
                      "daily_units": {"time": "iso8601", "weather_code": "wmo code", "temperature_2m_max": "°F", "temperature_2m_min": "°F"},
                      "daily": {"time": ["2025-10-26"], "weather_code": [0], "temperature_2m_max": [46.8], "temperature_2m_min": [32.5]}
                    }
                    """);
        }
        // Berlin, Germany: the archive API is down
        if (latitude == 52.52437 && longitude == 13.41053) {
            return MockResponse.status(503);
        }
        return MockResponse.notFound();
    }

}
