package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A request received by the mock server. {@code host} comes from the {@code Host} header,
 * i.e. it's the external host the app thinks it's talking to. {@code pathParams} are the named groups
 * of the matched route's path regex.
 */
public record MockRequest(String method, String host, String path, Map<String, String> pathParams,
                          Map<String, String> queryParams, String body) {

    static MockRequest from(HttpExchange exchange) throws IOException {
        String host = exchange.getRequestHeaders().getFirst("Host");
        return new MockRequest(
                exchange.getRequestMethod(),
                host == null ? "" : host.replaceFirst(":\\d+$", ""),
                exchange.getRequestURI().getPath(),
                Map.of(),
                parseQuery(exchange.getRequestURI().getRawQuery()),
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
    }

    MockRequest withPathParams(Map<String, String> pathParams) {
        return new MockRequest(method, host, path, pathParams, queryParams, body);
    }

    public String pathParam(String name) {
        return pathParams.get(name);
    }

    public String queryParam(String name) {
        return queryParams.get(name);
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> params = new LinkedHashMap<>();
        if (rawQuery != null && !rawQuery.isEmpty()) {
            for (String pair : rawQuery.split("&")) {
                String[] nameValue = pair.split("=", 2);
                params.put(URLDecoder.decode(nameValue[0], StandardCharsets.UTF_8),
                        nameValue.length > 1 ? URLDecoder.decode(nameValue[1], StandardCharsets.UTF_8) : "");
            }
        }
        return params;
    }

}
