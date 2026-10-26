package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.City;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.CurrentConditions;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Units;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.HitCounter;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final WeatherService weatherService;
    private final HitCounter hitCounter;

    public HomeController(WeatherService weatherService, HitCounter hitCounter) {
        this.weatherService = weatherService;
        this.hitCounter = hitCounter;
    }

    @GetMapping("/")
    public String home(@CookieValue(name = MyCities.COOKIE_NAME, required = false) String myCitiesCookie,
                       @CookieValue(name = Cookies.UNITS, required = false) String unitsCookie,
                       Model model) {
        Units units = Units.fromCookieValue(unitsCookie);
        List<SavedCity> myCities = new ArrayList<>();
        for (long id : MyCities.fromCookieValue(myCitiesCookie).ids()) {
            try {
                City city = weatherService.city(id);
                myCities.add(new SavedCity(city, weatherService.forecast(city, units).current()));
            } catch (RuntimeException e) {
                // Leave out a saved city we can't show right now rather than failing the front page
            }
        }
        model.addAttribute("myCities", myCities);
        model.addAttribute("hits", String.format("%06d", hitCounter.hit()));
        return "home";
    }

    public record SavedCity(City city, CurrentConditions current) {
    }

}
