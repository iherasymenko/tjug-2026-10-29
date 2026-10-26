package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

public record MockResponse(int status, String contentType, String body) {

    public static MockResponse json(String body) {
        return new MockResponse(200, "application/json", body);
    }

    public static MockResponse status(int status) {
        return new MockResponse(status, "text/plain", "");
    }

    public static MockResponse notFound() {
        return status(404);
    }

}
