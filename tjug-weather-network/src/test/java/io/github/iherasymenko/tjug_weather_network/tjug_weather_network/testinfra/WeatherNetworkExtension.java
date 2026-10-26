package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks.OorsprongCountryInfoService;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks.OpenMeteoAirQualityApi;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks.OpenMeteoArchiveApi;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks.OpenMeteoForecastApi;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks.OpenMeteoGeocodingApi;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

final class WeatherNetworkExtension implements ParameterResolver {

    private static final List<MockVirtualHost> VIRTUAL_HOSTS = List.of(
            new OpenMeteoGeocodingApi(),
            new OpenMeteoForecastApi(),
            new OpenMeteoAirQualityApi(),
            new OpenMeteoArchiveApi(),
            new OorsprongCountryInfoService()
    );

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(WeatherNetworkExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == WeatherNetwork.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return environment(extensionContext).application;
    }

    // Started once per JVM and stopped by JUnit when the root context closes
    private static Environment environment(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE)
                .computeIfAbsent(Environment.class, _ -> Environment.start(), Environment.class);
    }

    private static final class Environment implements AutoCloseable {

        private final Deque<Cleanup> cleanups = new ArrayDeque<>();
        private final WeatherNetwork application;

        // Order matters: the trust store and the proxy must be in place before the app creates its HTTP client
        private Environment() throws Exception {
            try {
                Path directory = Files.createTempDirectory("weather-network-test");
                cleanups.push(() -> deleteRecursively(directory));
                List<String> hosts = VIRTUAL_HOSTS.stream().map(MockVirtualHost::host).toList();
                CertificateGenerator certificate = new CertificateGenerator(directory, hosts);
                certificate.installAsJvmTrustStore();
                cleanups.push(certificate::uninstallAsJvmTrustStore);
                MockHttpServer mockServer = new MockHttpServer(certificate.serverContext(), VIRTUAL_HOSTS);
                cleanups.push(mockServer::stop);
                HttpProxy proxy = new HttpProxy(mockServer.httpsAddress(), mockServer.httpAddress());
                cleanups.push(proxy::stop);
                proxy.installAsJvmProxy();
                cleanups.push(proxy::uninstallAsJvmProxy);
                application = WeatherNetwork.start(directory.resolve("tomcat"));
                cleanups.push(application::stop);
            } catch (Exception e) {
                try {
                    close();
                } catch (Exception cleanupFailure) {
                    e.addSuppressed(cleanupFailure);
                }
                throw e;
            }
        }

        static Environment start() {
            try {
                return new Environment();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot start the test environment", e);
            }
        }

        @Override
        public void close() throws Exception {
            Exception failure = null;
            while (!cleanups.isEmpty()) {
                try {
                    cleanups.pop().run();
                } catch (Exception e) {
                    if (failure == null) {
                        failure = e;
                    } else {
                        failure.addSuppressed(e);
                    }
                }
            }
            if (failure != null) {
                throw failure;
            }
        }

        private static void deleteRecursively(Path directory) throws IOException {
            List<Path> paths;
            try (Stream<Path> walk = Files.walk(directory)) {
                paths = walk.sorted(Comparator.reverseOrder()).toList();
            }
            for (Path path : paths) {
                Files.delete(path);
            }
        }

        @FunctionalInterface
        private interface Cleanup {

            void run() throws Exception;

        }

    }

}
