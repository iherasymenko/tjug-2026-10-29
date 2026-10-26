package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.Units;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ControllerAdvice
public class GlobalModelAttributes {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH);
    private static final DateTimeFormatter FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);

    @ModelAttribute("units")
    public Units units(@CookieValue(name = Cookies.UNITS, required = false) String units) {
        return Units.fromCookieValue(units);
    }

    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return request.getQueryString() == null ? path : path + "?" + request.getQueryString();
    }

    @ModelAttribute("dateFormat")
    public DateTimeFormatter dateFormat() {
        return DATE_FORMAT;
    }

    @ModelAttribute("fullDateFormat")
    public DateTimeFormatter fullDateFormat() {
        return FULL_DATE_FORMAT;
    }

    @ModelAttribute("dateTimeFormat")
    public DateTimeFormatter dateTimeFormat() {
        return DATE_TIME_FORMAT;
    }

}
