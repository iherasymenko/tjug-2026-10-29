package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.City;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Forecast;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Units;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.CountryService;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CityController {

    private final WeatherService weatherService;
    private final CountryService countryService;

    public CityController(WeatherService weatherService, CountryService countryService) {
        this.weatherService = weatherService;
        this.countryService = countryService;
    }

    @GetMapping("/city")
    public String city(@RequestParam long id,
                       @CookieValue(name = MyCities.COOKIE_NAME, required = false) String myCitiesCookie,
                       @CookieValue(name = Cookies.UNITS, required = false) String unitsCookie,
                       Model model) {
        Units units = Units.fromCookieValue(unitsCookie);
        City city = weatherService.city(id);
        Forecast forecast = weatherService.forecast(city, units);
        model.addAttribute("city", city);
        model.addAttribute("forecast", forecast);
        model.addAttribute("airQuality", weatherService.airQuality(city).orElse(null));
        model.addAttribute("lastYear",
                weatherService.sameDayLastYear(city, forecast.current().time().toLocalDate(), units).orElse(null));
        model.addAttribute("country", countryService.countryProfile(city).orElse(null));
        model.addAttribute("inMyCities", MyCities.fromCookieValue(myCitiesCookie).contains(id));
        return "city";
    }

    @GetMapping("/rss/forecast.xml")
    public String rss(@RequestParam long id,
                      @CookieValue(name = Cookies.UNITS, required = false) String unitsCookie,
                      Model model) {
        City city = weatherService.city(id);
        model.addAttribute("city", city);
        model.addAttribute("forecast", weatherService.forecast(city, Units.fromCookieValue(unitsCookie)));
        return "rss";
    }

}
