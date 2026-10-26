package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockRequest;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockResponse;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockVirtualHost;

public final class OpenMeteoAirQualityApi extends MockVirtualHost {

    public OpenMeteoAirQualityApi() {
        get("/v1/air-quality", this::airQuality);
    }

    @Override
    protected String host() {
        return "air-quality-api.open-meteo.com";
    }

    private MockResponse airQuality(MockRequest request) {
        if (!"us_aqi,pm2_5,pm10".equals(request.queryParam("current"))) {
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

        // Toronto, Ontario: moderate air quality
        if (latitude == 43.70643 && longitude == -79.39864) {
            return MockResponse.json("""
                    {
                      "latitude": 43.699997, "longitude": -79.4, "generationtime_ms": 0.25,
                      "utc_offset_seconds": -14400, "timezone": "America/Toronto", "timezone_abbreviation": "GMT-4", "elevation": 165.0,
                      "current_units": {"time": "iso8601", "interval": "seconds", "us_aqi": "USAQI", "pm2_5": "μg/m³", "pm10": "μg/m³"},
                      "current": {"time": "2026-10-26T17:00", "interval": 3600, "us_aqi": 60, "pm2_5": 3.7, "pm10": 4.2}
                    }
                    """);
        }
        // Berlin, Germany: the air quality API is down
        if (latitude == 52.52437 && longitude == 13.41053) {
            return MockResponse.status(503);
        }
        return MockResponse.notFound();
    }

}
