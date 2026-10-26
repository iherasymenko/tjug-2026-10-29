package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.AirQuality;

public record OpenMeteoAirQualityResponse(Current current) {

    public record Current(@JsonProperty("us_aqi") int usAqi,
                          @JsonProperty("pm2_5") double pm25,
                          double pm10) {
    }

    public AirQuality toAirQuality() {
        return new AirQuality(current.usAqi(), current.pm25(), current.pm10());
    }

}
