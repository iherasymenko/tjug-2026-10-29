package io.github.iherasymenko.tjug_weather_network.tjug_weather_network_launcher;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.scan.StandardJarScanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Launcher {

    void main() throws Exception {
        // ============================================================================
        // This Launcher is THE place for all the manual hacks required to run this
        // project locally (paths, ports, temp dirs, classpath workarounds, etc.).
        //
        // Keep them HERE -- never leak them into the `tjug-weather-network` module.
        // ============================================================================
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory("tomcat").toString());
        tomcat.setPort(8080);
        // Otherwise Tomcat only logs a failed connector (e.g. port 8080 taken) and keeps running without it
        tomcat.getConnector().setThrowOnFailure(true);
        Context context = tomcat.addWebapp("", webappDir().toString());
        // The Metro jars list their dependencies in their manifests by plain file name, as if they all sat in one
        // directory; in ~/.m2 they don't, so following those entries only produces "Failed to scan" warnings
        ((StandardJarScanner) context.getJarScanner()).setScanManifest(false);

        tomcat.start();
        tomcat.getServer().await();
    }

    private Path webappDir() {
        return Stream.of("tjug-weather-network/src/main/webapp", "../tjug-weather-network/src/main/webapp")
                .map(dir -> Path.of(dir).toAbsolutePath().normalize())
                .filter(Files::isDirectory)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot find tjug-weather-network/src/main/webapp; run from the project root"));
    }

}
