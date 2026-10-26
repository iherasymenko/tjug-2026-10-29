package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WeatherNetworkExtension.class)
public @interface WeatherNetworkTest {
}
