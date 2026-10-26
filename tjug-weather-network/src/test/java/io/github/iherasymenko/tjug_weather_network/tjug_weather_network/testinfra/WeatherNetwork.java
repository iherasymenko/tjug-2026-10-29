package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.scan.StandardJarScanner;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

/**
 * Redirects aren't followed, so tests can check them.
 */
public final class WeatherNetwork {

    private final Tomcat tomcat;
    private final HttpClient httpClient = HttpClient.newBuilder().proxy(HttpClient.Builder.NO_PROXY).build();

    private WeatherNetwork(Tomcat tomcat) {
        this.tomcat = tomcat;
    }

    static WeatherNetwork start(Path baseDir) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.toString());
        tomcat.setPort(0);
        // Resolved the same way as the "localhost" in uri(), so the client always finds the address Tomcat bound
        tomcat.getConnector().setProperty("address", "localhost");
        Context context = tomcat.addWebapp("", Path.of("src/main/webapp").toAbsolutePath().toString());
        // The Metro jars list their dependencies in their manifests by plain file name, as if they all sat in one
        // directory; in ~/.m2 they don't, so following those entries only produces "Failed to scan" warnings
        ((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
        tomcat.start();
        return new WeatherNetwork(tomcat);
    }

    private URI uri(String pathAndQuery) {
        return URI.create("http://localhost:" + tomcat.getConnector().getLocalPort() + pathAndQuery);
    }

    /**
     * @param headers header names and values, alternating, e.g. {@code "Cookie", "units=imperial"}
     */
    public HttpResponse<String> get(String pathAndQuery, String... headers) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(uri(pathAndQuery));
        if (headers.length > 0) {
            request.headers(headers);
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * @param headers header names and values, alternating, e.g. {@code "Cookie", "units=imperial"}
     */
    public HttpResponse<String> postForm(String path, Map<String, String> form, String... headers) throws IOException, InterruptedException {
        StringBuilder body = new StringBuilder();
        for (var field : form.entrySet()) {
            if (!body.isEmpty()) {
                body.append('&');
            }
            body.append(URLEncoder.encode(field.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(field.getValue(), StandardCharsets.UTF_8));
        }
        HttpRequest.Builder request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        if (headers.length > 0) {
            request.headers(headers);
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    void stop() throws LifecycleException {
        httpClient.close();
        tomcat.stop();
        tomcat.destroy();
    }

}
