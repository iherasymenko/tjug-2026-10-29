package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@WeatherNetworkTest
class SearchPageTest {

    @Test
    void listsEveryMatchingCity(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/search?q=Toronto");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("Search results for &quot;Toronto&quot;")
                .contains("<a href=\"/city?id=6167865\">Toronto, Ontario, Canada</a>")
                .contains("<a href=\"/city?id=5174095\">Toronto, Ohio, United States</a>");
    }

    @Test
    void keepsTheQueryInTheSearchBox(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/search?q=Berlin");

        assertThat(response.body()).contains("name=\"q\" size=\"30\" value=\"Berlin\"");
    }

    @Test
    void saysWhenNothingMatches(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/search?q=Atlantis");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Sorry, we couldn't find any cities matching &quot;Atlantis&quot;");
    }

    @Test
    void escapesTheQuery(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/search?q=%3Cb%3Ebold%3C%2Fb%3E");

        assertThat(response.body())
                .contains("&lt;b&gt;bold&lt;/b&gt;")
                .doesNotContain("<b>bold</b>");
    }

    @Test
    void sendsEmptySearchesBackHome(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/search?q=+");

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("Location")).hasValue("/");
    }

}
