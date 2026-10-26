package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final WeatherService weatherService;

    public SearchController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", defaultValue = "") String query, Model model) {
        if (query.isBlank()) {
            return "redirect:/";
        }
        model.addAttribute("query", query.strip());
        model.addAttribute("cities", weatherService.searchCities(query.strip()));
        return "search";
    }

}
