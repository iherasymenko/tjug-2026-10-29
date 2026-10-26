package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Everything one external host does in tests. Subclasses register their routes in the constructor,
 * mapping an HTTP method and a path regex to one of their methods:
 * <pre>{@code
 * public OpenMeteo() {
 *     get("/v1/forecast", this::forecast);
 * }
 * }</pre>
 * The regex must match the whole path; named groups are available via {@link MockRequest#pathParam(String)}.
 * Routes are tried in registration order and the first match wins; anything unmatched gets a 404.
 * <p>
 * Implementations are stateless: every scenario a test needs is encoded in the class
 * and selected purely by what's in the request (e.g. well-known input values).
 */
public abstract class MockVirtualHost {

    private final List<Route> routes = new ArrayList<>();

    protected abstract String host();

    protected final void get(String pathRegex, Function<MockRequest, MockResponse> handler) {
        routes.add(new Route("GET", Pattern.compile(pathRegex), handler));
    }

    protected final void post(String pathRegex, Function<MockRequest, MockResponse> handler) {
        routes.add(new Route("POST", Pattern.compile(pathRegex), handler));
    }

    protected final void put(String pathRegex, Function<MockRequest, MockResponse> handler) {
        routes.add(new Route("PUT", Pattern.compile(pathRegex), handler));
    }

    protected final void delete(String pathRegex, Function<MockRequest, MockResponse> handler) {
        routes.add(new Route("DELETE", Pattern.compile(pathRegex), handler));
    }

    final MockResponse respond(MockRequest request) {
        for (Route route : routes) {
            if (!route.method().equals(request.method())) {
                continue;
            }
            Matcher matcher = route.path().matcher(request.path());
            if (matcher.matches()) {
                return route.handler().apply(request.withPathParams(namedGroups(matcher)));
            }
        }
        return MockResponse.notFound();
    }

    private static Map<String, String> namedGroups(Matcher matcher) {
        Map<String, String> groups = new HashMap<>();
        for (var group : matcher.namedGroups().entrySet()) {
            groups.put(group.getKey(), matcher.group(group.getValue()));
        }
        return groups;
    }

    private record Route(String method, Pattern path, Function<MockRequest, MockResponse> handler) { }

}
