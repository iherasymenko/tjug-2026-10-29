package io.github.iherasymenko.tjug_weather_network.tjug_weather_network;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetwork;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.WeatherNetworkTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// The guestbook lives as long as the app does, so every test signs it under a name of its own
@WeatherNetworkTest
class GuestbookTest {

    @Test
    void showsTheMessagesSigned(WeatherNetwork app) throws Exception {
        String name = uniqueName();

        HttpResponse<String> signed = app.postForm("/guestbook", Map.of("name", name, "message", "Great site!"));

        assertThat(signed.statusCode()).isEqualTo(302);
        assertThat(signed.headers().firstValue("Location")).hasValue("/guestbook");
        assertThat(app.get("/guestbook").body())
                .contains("<b>" + name + "</b> wrote on")
                .contains("Great site!");
    }

    @Test
    void showsTheNewestMessagesFirst(WeatherNetwork app) throws Exception {
        String older = uniqueName();
        String newer = uniqueName();

        app.postForm("/guestbook", Map.of("name", older, "message", "First!"));
        app.postForm("/guestbook", Map.of("name", newer, "message", "Second!"));

        String page = app.get("/guestbook").body();
        assertThat(page.indexOf(newer)).isLessThan(page.indexOf(older));
    }

    @Test
    void escapesWhatVisitorsWrite(WeatherNetwork app) throws Exception {
        String name = uniqueName();

        app.postForm("/guestbook", Map.of("name", name, "message", "<script>alert('hi')</script>"));

        assertThat(app.get("/guestbook").body())
                .contains("&lt;script&gt;alert(&#039;hi&#039;)&lt;/script&gt;")
                .doesNotContain("<script>alert");
    }

    @Test
    void asksForANameAndAMessage(WeatherNetwork app) throws Exception {
        String name = uniqueName();

        HttpResponse<String> response = app.postForm("/guestbook", Map.of("name", name, "message", "  "));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("Please enter your name and a message.")
                .contains("value=\"" + name + "\"");
        assertThat(app.get("/guestbook").body()).doesNotContain("<b>" + name + "</b>");
    }

    @Test
    void limitsTheLengthOfNames(WeatherNetwork app) throws Exception {
        HttpResponse<String> response = app.postForm("/guestbook", Map.of("name", "x".repeat(51), "message", "Hi"));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Your name can be at most 50 characters long.");
    }

    private static String uniqueName() {
        return "Visitor " + UUID.randomUUID().toString().substring(0, 8);
    }

}
