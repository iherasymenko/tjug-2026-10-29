package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockRequest;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockResponse;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockVirtualHost;

public final class OpenMeteoForecastApi extends MockVirtualHost {

    public OpenMeteoForecastApi() {
        get("/v1/forecast", this::forecast);
    }

    @Override
    protected String host() {
        return "api.open-meteo.com";
    }

    private MockResponse forecast(MockRequest request) {
        if (!"temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m".equals(request.queryParam("current"))
                || !"weather_code,temperature_2m_max,temperature_2m_min".equals(request.queryParam("daily"))
                || !"auto".equals(request.queryParam("timezone"))) {
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
        String units = request.queryParam("temperature_unit") + "/" + request.queryParam("wind_speed_unit");

        // Toronto, Ontario, in metric units: one day of every kind of weather
        if (latitude == 43.70643 && longitude == -79.39864 && units.equals("celsius/kmh")) {
            return MockResponse.json("""
                    {
                      "latitude": 43.70455, "longitude": -79.404625, "generationtime_ms": 1.42,
                      "utc_offset_seconds": -14400, "timezone": "America/Toronto", "timezone_abbreviation": "GMT-4", "elevation": 165.0,
                      "current_units": {"time": "iso8601", "interval": "seconds", "temperature_2m": "°C", "relative_humidity_2m": "%",
                                        "weather_code": "wmo code", "wind_speed_10m": "km/h"},
                      "current": {"time": "2026-10-26T17:30", "interval": 900, "temperature_2m": 13.2, "relative_humidity_2m": 48,
                                  "weather_code": 1, "wind_speed_10m": 15.1},
                      "daily_units": {"time": "iso8601", "weather_code": "wmo code", "temperature_2m_max": "°C", "temperature_2m_min": "°C"},
                      "daily": {
                        "time": ["2026-10-26", "2026-10-27", "2026-10-28", "2026-10-29", "2026-10-30", "2026-10-31", "2026-11-01"],
                        "weather_code": [1, 2, 3, 45, 61, 71, 95],
                        "temperature_2m_max": [15.7, 8.4, 14.6, 21.8, 16.2, 11.4, 12.1],
                        "temperature_2m_min": [6.2, 1.9, 5.4, 9.7, 8.9, 7.8, 10.6]
                      }
                    }
                    """);
        }
        // Toronto, Ontario, in imperial units: the same weather as above
        if (latitude == 43.70643 && longitude == -79.39864 && units.equals("fahrenheit/mph")) {
            return MockResponse.json("""
                    {
                      "latitude": 43.70455, "longitude": -79.404625, "generationtime_ms": 1.10,
                      "utc_offset_seconds": -14400, "timezone": "America/Toronto", "timezone_abbreviation": "GMT-4", "elevation": 165.0,
                      "current_units": {"time": "iso8601", "interval": "seconds", "temperature_2m": "°F", "relative_humidity_2m": "%",
                                        "weather_code": "wmo code", "wind_speed_10m": "mp/h"},
                      "current": {"time": "2026-10-26T17:30", "interval": 900, "temperature_2m": 55.8, "relative_humidity_2m": 48,
                                  "weather_code": 1, "wind_speed_10m": 9.4},
                      "daily_units": {"time": "iso8601", "weather_code": "wmo code", "temperature_2m_max": "°F", "temperature_2m_min": "°F"},
                      "daily": {
                        "time": ["2026-10-26", "2026-10-27", "2026-10-28", "2026-10-29", "2026-10-30", "2026-10-31", "2026-11-01"],
                        "weather_code": [1, 2, 3, 45, 61, 71, 95],
                        "temperature_2m_max": [60.3, 47.1, 58.3, 71.2, 61.2, 52.5, 53.8],
                        "temperature_2m_min": [43.2, 35.4, 41.7, 49.4, 48.0, 46.0, 51.1]
                      }
                    }
                    """);
        }
        // Berlin, Germany, in metric units: overcast with some rain
        if (latitude == 52.52437 && longitude == 13.41053 && units.equals("celsius/kmh")) {
            return MockResponse.json("""
                    {
                      "latitude": 52.528915, "longitude": 13.423877, "generationtime_ms": 0.59,
                      "utc_offset_seconds": 3600, "timezone": "Europe/Berlin", "timezone_abbreviation": "GMT+1", "elevation": 46.0,
                      "current_units": {"time": "iso8601", "interval": "seconds", "temperature_2m": "°C", "relative_humidity_2m": "%",
                                        "weather_code": "wmo code", "wind_speed_10m": "km/h"},
                      "current": {"time": "2026-10-26T22:30", "interval": 900, "temperature_2m": 7.7, "relative_humidity_2m": 80,
                                  "weather_code": 3, "wind_speed_10m": 8.0},
                      "daily_units": {"time": "iso8601", "weather_code": "wmo code", "temperature_2m_max": "°C", "temperature_2m_min": "°C"},
                      "daily": {
                        "time": ["2026-10-26", "2026-10-27", "2026-10-28", "2026-10-29", "2026-10-30", "2026-10-31", "2026-11-01"],
                        "weather_code": [3, 3, 61, 61, 3, 3, 61],
                        "temperature_2m_max": [13.0, 14.7, 14.8, 12.4, 12.5, 13.0, 10.7],
                        "temperature_2m_min": [7.5, 7.5, 7.8, 7.4, 8.8, 8.7, 6.8]
                      }
                    }
                    """);
        }
        // Kyiv, Ukraine, in metric units: a rainy week, just after midnight local time
        if (latitude == 50.45466 && longitude == 30.5238 && units.equals("celsius/kmh")) {
            return MockResponse.json("""
                    {
                      "latitude": 50.423317, "longitude": 30.491209, "generationtime_ms": 0.31,
                      "utc_offset_seconds": 7200, "timezone": "Europe/Kyiv", "timezone_abbreviation": "GMT+2", "elevation": 188.0,
                      "current_units": {"time": "iso8601", "interval": "seconds", "temperature_2m": "°C", "relative_humidity_2m": "%",
                                        "weather_code": "wmo code", "wind_speed_10m": "km/h"},
                      "current": {"time": "2026-10-27T00:15", "interval": 900, "temperature_2m": 9.7, "relative_humidity_2m": 85,
                                  "weather_code": 61, "wind_speed_10m": 6.3},
                      "daily_units": {"time": "iso8601", "weather_code": "wmo code", "temperature_2m_max": "°C", "temperature_2m_min": "°C"},
                      "daily": {
                        "time": ["2026-10-27", "2026-10-28", "2026-10-29", "2026-10-30", "2026-10-31", "2026-11-01", "2026-11-02"],
                        "weather_code": [61, 61, 61, 3, 61, 3, 3],
                        "temperature_2m_max": [11.4, 9.4, 9.0, 8.9, 9.6, 9.1, 7.9],
                        "temperature_2m_min": [8.0, 6.3, 6.1, 6.7, 5.4, 4.8, 4.1]
                      }
                    }
                    """);
        }
        // "Null Island": the forecast API is down
        if (latitude == 0 && longitude == 0) {
            return MockResponse.status(503);
        }
        return MockResponse.notFound();
    }

}
