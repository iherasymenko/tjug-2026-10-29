# TJUG Weather Network

Demo project for a TJUG talk on dealing with legacy software. It contains:

- **The app**: a deliberately old-school weather portal. It's a Spring MVC webapp (WAR) with JSPs and `web.xml`, and it uses four Open-Meteo APIs and one SOAP web service.
- **Black-box tests**: they start the unmodified app and send every outgoing call through a proxy to mock servers, all inside the test JVM.

## Build

You need JDK 27; Maven comes with the project through the wrapper.

```shell
./mvnw clean package
```

## Run

```shell
./mvnw clean package -Prun
```

The app starts at <http://localhost:8080/>. From the IDE, run `io.github.iherasymenko.tjug_weather_network.tjug_weather_network_launcher.Launcher` in the `tjug-weather-network-launcher` module instead.

You can also deploy `tjug-weather-network/target/tjug-weather-network-1.0-SNAPSHOT.war` to a standalone Tomcat 11.

## License

[MIT No Attribution](LICENSE): copy anything you like into your own projects, no attribution needed.

A few files aren't covered by it:

- The Maven wrapper scripts (`mvnw`, `mvnw.cmd`) are under the Apache License 2.0.
- `CountryInfoService.wsdl` is the public description of oorsprong.org's web service.
- The canned API responses in the test mocks are data from [Open-Meteo](https://open-meteo.com/), licensed CC BY 4.0.
