package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@WeatherNetworkTest
class MyCitiesTest {

    @Test
    void savesACityInACookie(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.postForm("/mycities/add", Map.of("id", "6167865"));

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("Location")).hasValue("/city?id=6167865");
        assertThat(response.headers().firstValue("Set-Cookie")).hasValueSatisfying(cookie -> assertThat(cookie)
                .startsWith("mycities=6167865;")
                .contains("Max-Age=31536000", "Path=/", "HttpOnly"));
    }

    @Test
    void addsToTheSavedCities(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.postForm("/mycities/add", Map.of("id", "6167865"), "Cookie", "mycities=2950159");

        assertThat(response.headers().firstValue("Set-Cookie"))
                .hasValueSatisfying(cookie -> assertThat(cookie).startsWith("mycities=2950159|6167865;"));
    }

    @Test
    void doesNotSaveCitiesThatDoNotExist(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.postForm("/mycities/add", Map.of("id", "1"));

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.headers().firstValue("Set-Cookie")).isEmpty();
    }

    @Test
    void removesASavedCity(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.postForm("/mycities/remove", Map.of("id", "6167865"), "Cookie", "mycities=6167865|2950159");

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("Location")).hasValue("/city?id=6167865");
        assertThat(response.headers().firstValue("Set-Cookie"))
                .hasValueSatisfying(cookie -> assertThat(cookie).startsWith("mycities=2950159;"));
    }

    @Test
    void keepsTheTenMostRecentlySavedCities(WeatherNetwork app) throws Exception {
        String tenCities = "mycities=1|2|3|4|5|6|7|8|9|10";

        HttpResponse<String> response = app.postForm("/mycities/add", Map.of("id", "6167865"), "Cookie", tenCities);

        assertThat(response.headers().firstValue("Set-Cookie"))
                .hasValueSatisfying(cookie -> assertThat(cookie).startsWith("mycities=2|3|4|5|6|7|8|9|10|6167865;"));
    }

}
