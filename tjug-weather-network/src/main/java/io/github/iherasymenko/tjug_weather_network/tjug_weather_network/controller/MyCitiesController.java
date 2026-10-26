package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyCitiesController {

    private final WeatherService weatherService;

    public MyCitiesController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/mycities/add")
    public String add(@RequestParam long id,
                      @CookieValue(name = MyCities.COOKIE_NAME, required = false) String myCitiesCookie,
                      HttpServletRequest request, HttpServletResponse response) {
        // Only real cities make it into the cookie
        weatherService.city(id);
        MyCities myCities = MyCities.fromCookieValue(myCitiesCookie).add(id);
        Cookies.set(request, response, MyCities.COOKIE_NAME, myCities.cookieValue());
        return "redirect:/city?id=" + id;
    }

    @PostMapping("/mycities/remove")
    public String remove(@RequestParam long id,
                         @CookieValue(name = MyCities.COOKIE_NAME, required = false) String myCitiesCookie,
                         HttpServletRequest request, HttpServletResponse response) {
        MyCities myCities = MyCities.fromCookieValue(myCitiesCookie).remove(id);
        Cookies.set(request, response, MyCities.COOKIE_NAME, myCities.cookieValue());
        return "redirect:/city?id=" + id;
    }

}
