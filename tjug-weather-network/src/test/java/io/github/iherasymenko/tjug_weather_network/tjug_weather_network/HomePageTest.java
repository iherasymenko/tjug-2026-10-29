package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@WeatherNetworkTest
class HomePageTest {

    private static final Pattern HIT_COUNTER = Pattern.compile("<span class=\"hit-counter\">(\\d+)</span>");

    @Test
    void welcomesVisitorsWithASearchBox(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("Welcome to the TJUG Weather Network!")
                .contains("<form class=\"search\" action=\"/search\" method=\"get\">");
    }

    @Test
    void countsVisitors(WeatherNetwork app) throws Exception {
        long first = hits(app.get("/").body());
        long second = hits(app.get("/").body());

        // Other tests may visit the front page in between
        assertThat(second).isGreaterThan(first);
    }

    @Test
    void explainsMyCitiesToNewVisitors(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/");

        assertThat(response.body()).contains("You haven't saved any cities yet.");
    }

    @Test
    void showsMyCitiesWithTheirCurrentWeather(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/", "Cookie", "mycities=6167865|2950159");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("<a href=\"/city?id=6167865\">Toronto, Ontario, Canada</a>")
                .contains("<a href=\"/city?id=2950159\">Berlin, State of Berlin, Germany</a>")
                .contains("13°C", "8°C");
    }

    @Test
    void showsMyCitiesInTheChosenUnits(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/", "Cookie", "mycities=6167865; units=imperial");

        assertThat(response.body()).contains("56°F");
    }

    @Test
    void leavesOutSavedCitiesItCannotShow(WeatherNetwork app) throws Exception {
        // 1 isn't a city id, and Null Island's forecast is down
        HttpResponse<String> response = app.get("/", "Cookie", "mycities=1|9999999|6167865");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("Toronto, Ontario, Canada")
                .doesNotContain("Null Island");
    }

    @Test
    void servesTheStylesheetAndWeatherIcons(WeatherNetwork app) throws Exception {
        assertThat(app.get("/css/style.css").headers().firstValue("Content-Type"))
                .hasValueSatisfying(contentType -> assertThat(contentType).startsWith("text/css"));
        assertThat(app.get("/images/weather/clear.gif").headers().firstValue("Content-Type"))
                .hasValue("image/gif");
    }

    @Test
    void servesTheFavicon(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/favicon.ico");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValue("image/x-icon");
    }

    private static long hits(String body) {
        Matcher matcher = HIT_COUNTER.matcher(body);
        assertThat(matcher.find()).as("hit counter on the page").isTrue();
        return Long.parseLong(matcher.group(1));
    }

}
