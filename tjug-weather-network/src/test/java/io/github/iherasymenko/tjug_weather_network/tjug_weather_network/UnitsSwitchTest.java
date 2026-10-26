package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@WeatherNetworkTest
class UnitsSwitchTest {

    @Test
    void remembersTheChoiceAndGoesBack(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/units?system=imperial&back=%2Fcity%3Fid%3D6167865");

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("Location")).hasValue("/city?id=6167865");
        assertThat(response.headers().firstValue("Set-Cookie"))
                .hasValueSatisfying(cookie -> assertThat(cookie).startsWith("units=imperial;"));
    }

    @Test
    void neverRedirectsToAnotherSite(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/units?system=metric&back=%2F%2Fevil.example.com%2F");

        assertThat(response.headers().firstValue("Location")).hasValue("/");
    }

    @Test
    void offersTheOtherUnitsOnEveryPage(WeatherNetwork app) throws Exception {
        String metricPage = app.get("/city?id=6167865").body();
        String imperialPage = app.get("/city?id=6167865", "Cookie", "units=imperial").body();

        assertThat(metricPage).contains("<a href=\"/units?system=imperial&back=%2fcity%3fid%3d6167865\">&deg;F</a>");
        assertThat(imperialPage).contains("<a href=\"/units?system=metric&back=%2fcity%3fid%3d6167865\">&deg;C</a>");
    }

}
