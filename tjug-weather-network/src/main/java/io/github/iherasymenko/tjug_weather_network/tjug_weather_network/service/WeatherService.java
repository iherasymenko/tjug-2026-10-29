package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoAirQualityClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoArchiveClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoForecastClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoGeocodingClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.AirQuality;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.City;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.DailyWeather;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Forecast;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Units;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {

    private static final Log log = LogFactory.getLog(WeatherService.class);

    private final OpenMeteoGeocodingClient geocodingClient;
    private final OpenMeteoForecastClient forecastClient;
    private final OpenMeteoAirQualityClient airQualityClient;
    private final OpenMeteoArchiveClient archiveClient;

    public WeatherService(OpenMeteoGeocodingClient geocodingClient, OpenMeteoForecastClient forecastClient,
                          OpenMeteoAirQualityClient airQualityClient, OpenMeteoArchiveClient archiveClient) {
        this.geocodingClient = geocodingClient;
        this.forecastClient = forecastClient;
        this.airQualityClient = airQualityClient;
        this.archiveClient = archiveClient;
    }

    public List<City> searchCities(String name) {
        return geocodingClient.search(name).toCities();
    }

    public City city(long id) {
        try {
            return geocodingClient.get(id).toCity();
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.NotFound e) {
            throw new CityNotFoundException(id);
        }
    }

    public Forecast forecast(City city, Units units) {
        return forecastClient.forecast(city.latitude(), city.longitude(),
                units.openMeteoTemperatureUnit(), units.openMeteoWindSpeedUnit()).toForecast();
    }

    public Optional<AirQuality> airQuality(City city) {
        try {
            return Optional.of(airQualityClient.current(city.latitude(), city.longitude()).toAirQuality());
        } catch (RestClientException e) {
            log.warn("Air quality unavailable for " + city.displayName(), e);
            return Optional.empty();
        }
    }

    /**
     * @param today the current date at the city
     */
    public Optional<DailyWeather> sameDayLastYear(City city, LocalDate today, Units units) {
        LocalDate lastYear = today.minusYears(1);
        try {
            List<DailyWeather> days = archiveClient.daily(city.latitude(), city.longitude(), lastYear, lastYear,
                    units.openMeteoTemperatureUnit()).daily().toDailyWeather();
            return days.stream().findFirst();
        } catch (RestClientException e) {
            log.warn("Historical weather unavailable for " + city.displayName(), e);
            return Optional.empty();
        }
    }

}
