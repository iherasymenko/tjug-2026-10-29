package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.function.Function.*;

final class MockHttpServer {

    private static final Log log = LogFactory.getLog(MockHttpServer.class);

    private final HttpsServer httpsServer;
    private final HttpServer httpServer;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, MockVirtualHost> virtualHosts;

    MockHttpServer(SSLContext sslContext, List<MockVirtualHost> virtualHosts) throws IOException {
        this.virtualHosts = virtualHosts.stream().collect(Collectors.toUnmodifiableMap(MockVirtualHost::host, identity()));
        httpsServer = HttpsServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));
        httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        for (HttpServer server : List.of(httpsServer, httpServer)) {
            server.setExecutor(executor);
            server.createContext("/", this::handle);
            server.start();
        }
    }

    InetSocketAddress httpsAddress() {
        return httpsServer.getAddress();
    }

    InetSocketAddress httpAddress() {
        return httpServer.getAddress();
    }

    private void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            MockRequest request = MockRequest.from(exchange);
            MockVirtualHost virtualHost = virtualHosts.get(request.host());
            MockResponse response;
            try {
                response = virtualHost == null ? MockResponse.notFound() : virtualHost.respond(request);
            } catch (RuntimeException e) {
                // Left uncaught, HttpServer drops the connection and logs the failure only at TRACE level
                log.error("Mock for " + request.host() + " failed on " + request.method() + " " + request.path(), e);
                response = MockResponse.status(500);
            }

            byte[] body = response.body().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", response.contentType());
            exchange.sendResponseHeaders(response.status(), body.length == 0 ? -1 : body.length);
            if (body.length > 0) {
                exchange.getResponseBody().write(body);
            }
        }
    }

    void stop() {
        httpsServer.stop(0);
        httpServer.stop(0);
        executor.close();
    }

}
