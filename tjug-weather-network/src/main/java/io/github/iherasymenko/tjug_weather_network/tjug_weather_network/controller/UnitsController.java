package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Units;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Pattern;

@Controller
public class UnitsController {

    // A path on this site, so the switch can't be used to redirect visitors elsewhere
    private static final Pattern LOCAL_PATH = Pattern.compile("/(?![/\\\\])[A-Za-z0-9/?=&._%+-]*");

    @GetMapping("/units")
    public String switchUnits(@RequestParam String system,
                              @RequestParam(defaultValue = "/") String back,
                              HttpServletRequest request, HttpServletResponse response) {
        Cookies.set(request, response, Cookies.UNITS, Units.fromCookieValue(system).cookieValue());
        return "redirect:" + (LOCAL_PATH.matcher(back).matches() ? back : "/");
    }

}
