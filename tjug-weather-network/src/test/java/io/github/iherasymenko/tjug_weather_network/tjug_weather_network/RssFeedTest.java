package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj3.XmlAssert;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@WeatherNetworkTest
class RssFeedTest {

    @Test
    void publishesTheForecastAsRss(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/rss/forecast.xml?id=6167865");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValue("application/rss+xml;charset=UTF-8");

        XmlAssert feed = XmlAssert.assertThat(response.body());
        feed.nodesByXPath("/rss").haveAttribute("version", "2.0");
        feed.valueByXPath("/rss/channel/title").isEqualTo("TJUG Weather Network: Toronto, Ontario, Canada");
        feed.valueByXPath("/rss/channel/link").endsWith("/city?id=6167865");
        feed.nodesByXPath("/rss/channel/item/title")
                .extractingText()
                .hasSize(7)
                .startsWith("Mon, Oct 26: Clear, 6°C to 16°C")
                .endsWith("Sun, Nov 1: Thunderstorm, 11°C to 12°C");
        feed.valueByXPath("/rss/channel/item[1]/guid").isEqualTo("city-6167865-2026-10-26");
    }

    @Test
    void saysWhenTheCityDoesNotExist(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.get("/rss/forecast.xml?id=1");

        assertThat(response.statusCode()).isEqualTo(404);
    }

}
