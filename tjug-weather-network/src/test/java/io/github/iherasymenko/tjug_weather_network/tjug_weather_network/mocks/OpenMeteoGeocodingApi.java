package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockRequest;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockResponse;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockVirtualHost;

public final class OpenMeteoGeocodingApi extends MockVirtualHost {

    public OpenMeteoGeocodingApi() {
        get("/v1/search", this::search);
        get("/v1/get", this::location);
    }

    @Override
    protected String host() {
        return "geocoding-api.open-meteo.com";
    }

    private MockResponse search(MockRequest request) {
        if (!"json".equals(request.queryParam("format"))) {
            return MockResponse.notFound();
        }
        String name = request.queryParam("name");

        // Toronto: the one in Ontario and the one in Ohio
        if ("toronto".equalsIgnoreCase(name)) {
            return MockResponse.json("""
                    {
                      "results": [
                        {"id": 6167865, "name": "Toronto", "latitude": 43.70643, "longitude": -79.39864, "elevation": 161.0,
                         "feature_code": "PPLA", "country_code": "CA", "timezone": "America/Toronto", "population": 2794356,
                         "country": "Canada", "admin1": "Ontario"},
                        {"id": 5174095, "name": "Toronto", "latitude": 40.46423, "longitude": -80.60091, "elevation": 214.0,
                         "feature_code": "PPL", "country_code": "US", "timezone": "America/New_York", "population": 4882,
                         "country": "United States", "admin1": "Ohio", "admin2": "Jefferson"}
                      ],
                      "generationtime_ms": 0.35
                    }
                    """);
        }
        // Berlin, Germany
        if ("berlin".equalsIgnoreCase(name)) {
            return MockResponse.json("""
                    {
                      "results": [
                        {"id": 2950159, "name": "Berlin", "latitude": 52.52437, "longitude": 13.41053, "elevation": 74.0,
                         "feature_code": "PPLC", "country_code": "DE", "timezone": "Europe/Berlin", "population": 3426354,
                         "country": "Germany", "admin1": "State of Berlin"}
                      ],
                      "generationtime_ms": 0.35
                    }
                    """);
        }
        // Nothing found: the real API leaves out "results" altogether
        return MockResponse.json("""
                {"generationtime_ms": 0.16}
                """);
    }

    private MockResponse location(MockRequest request) {
        String id = request.queryParam("id");

        // Toronto, Ontario
        if ("6167865".equals(id)) {
            return MockResponse.json("""
                    {"id": 6167865, "name": "Toronto", "latitude": 43.70643, "longitude": -79.39864, "elevation": 161.0,
                     "feature_code": "PPLA", "country_code": "CA", "timezone": "America/Toronto", "population": 2794356,
                     "country": "Canada", "admin1": "Ontario"}
                    """);
        }
        // Berlin, Germany
        if ("2950159".equals(id)) {
            return MockResponse.json("""
                    {"id": 2950159, "name": "Berlin", "latitude": 52.52437, "longitude": 13.41053, "elevation": 74.0,
                     "feature_code": "PPLC", "country_code": "DE", "timezone": "Europe/Berlin", "population": 3426354,
                     "country": "Germany", "admin1": "State of Berlin"}
                    """);
        }
        // Kyiv, Ukraine
        if ("703448".equals(id)) {
            return MockResponse.json("""
                    {"id": 703448, "name": "Kyiv", "latitude": 50.45466, "longitude": 30.5238, "elevation": 187.0,
                     "feature_code": "PPLC", "country_code": "UA", "timezone": "Europe/Kyiv", "population": 2952301,
                     "country": "Ukraine", "admin1": "Kyiv City"}
                    """);
        }
        // "Null Island" at 0, 0, where every weather API is down
        if ("9999999".equals(id)) {
            return MockResponse.json("""
                    {"id": 9999999, "name": "Null Island", "latitude": 0.0, "longitude": 0.0, "elevation": 0.0,
                     "feature_code": "ISL", "timezone": "GMT"}
                    """);
        }
        // Unknown id: the real API answers 400
        return new MockResponse(400, "application/json", """
                {"reason": "Location ID not found.", "error": true}
                """);
    }

}
