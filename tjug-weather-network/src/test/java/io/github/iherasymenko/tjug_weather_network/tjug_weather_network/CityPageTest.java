package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@WeatherNetworkTest
class CityPageTest {

    @Test
    void showsTheCurrentConditions(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("<h1>Toronto, Ontario, Canada</h1>")
                .contains("<span class=\"temperature\">13°C</span>")
                .contains("Humidity: 48%")
                .contains("Wind: 15 km/h")
                .contains("Local time: Oct 26, 2026 5:30 PM");
    }

    @Test
    void showsTheSevenDayForecast(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.body())
                .contains("<td>Mon, Oct 26</td>", "<td>Sun, Nov 1</td>")
                .contains("/images/weather/clear.gif", "/images/weather/partly-cloudy.gif", "/images/weather/cloudy.gif",
                        "/images/weather/fog.gif", "/images/weather/rain.gif", "/images/weather/snow.gif",
                        "/images/weather/thunderstorm.gif")
                .contains("Partly cloudy", "Overcast", "Fog", "Rain", "Snow", "Thunderstorm")
                .contains("<td>6°C</td>", "<td>16°C</td>", "<td>22°C</td>");
    }

    @Test
    void showsTheAirQuality(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.body())
                .contains("US AQI: <b>60</b> (Moderate)")
                .contains("PM2.5: 3.7 &micro;g/m&sup3;", "PM10: 4.2 &micro;g/m&sup3;");
    }

    @Test
    void showsThisDayLastYear(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.body()).containsPattern("On October 26, 2025:\\s+Clear,\\s+low 0°C,\\s+high 8°C");
    }

    @Test
    void showsTheCountryProfile(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.body())
                .contains("<b>About Canada</b>")
                .contains("Capital: Ottawa", "Currency: CAD", "Languages: English, French", "Dialing code: +1")
                .contains("<img src=\"http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Canada.jpg\"");
    }

    @Test
    void spellsKyivTheRightWay(WeatherNetwork app) throws Exception {
        // The country info service still says "Kiev"
        HttpResponse<String> response = app.get("/city?id=703448");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("<b>About Ukraine</b>")
                .contains("Capital: Kyiv")
                .doesNotContain("Kiev");
    }

    @Test
    void usesImperialUnitsWhenChosen(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865", "Cookie", "units=imperial");

        assertThat(response.body())
                .contains("<span class=\"temperature\">56°F</span>")
                .contains("Wind: 9 mph")
                .contains("<td>43°F</td>", "<td>60°F</td>")
                .containsPattern("low 32°F,\\s+high 47°F");
    }

    @Test
    void stillShowsTheForecastWhenTheExtrasAreDown(WeatherNetwork app) throws Exception {
        // Berlin's air quality and history APIs are down, and the country info service faults for Germany
        HttpResponse<String> response = app.get("/city?id=2950159");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("<span class=\"temperature\">8°C</span>")
                .contains("Air quality data is currently unavailable.")
                .contains("Historical data is currently unavailable.")
                .contains("Country information is currently unavailable.");
    }

    @Test
    void apologizesWhenTheForecastIsDown(WeatherNetwork app) throws Exception {
        // Null Island's forecast API is down
        HttpResponse<String> response = app.get("/city?id=9999999");

        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(response.body()).contains("Our weather station is experiencing technical difficulties.");
    }

    @Test
    void saysWhenTheCityDoesNotExist(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=1");

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).contains("Sorry, we couldn't find what you were looking for.", "City not found");
    }

    @Test
    void rejectsIdsThatAreNotNumbers(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=toronto");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("Sorry, we couldn't make sense of that request.");
    }

    @Test
    void offersToAddTheCityToMyCities(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.body())
                .contains("action=\"/mycities/add\"")
                .contains("Add to My Cities");
    }

    @Test
    void offersToRemoveASavedCity(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865", "Cookie", "mycities=2950159|6167865");

        assertThat(response.body())
                .contains("action=\"/mycities/remove\"")
                .contains("Remove from My Cities");
    }

    @Test
    void linksToTheRssFeed(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/city?id=6167865");

        assertThat(response.body()).contains("<a href=\"/rss/forecast.xml?id=6167865\">");
    }

}
