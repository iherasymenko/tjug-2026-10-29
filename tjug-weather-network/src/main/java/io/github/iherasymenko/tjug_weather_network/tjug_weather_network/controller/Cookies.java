package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class Cookies {

    static final String UNITS = "units";

    private static final int ONE_YEAR = 365 * 24 * 60 * 60;

    private Cookies() {
    }

    static void set(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(ONE_YEAR);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

}
