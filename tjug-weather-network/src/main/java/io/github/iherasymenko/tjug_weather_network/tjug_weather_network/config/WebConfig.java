package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.config;

import com.sun.xml.ws.developer.JAXWSProperties;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoAirQualityClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoArchiveClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoForecastClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.OpenMeteoGeocodingClient;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.countryinfo.CountryInfoService;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.countryinfo.CountryInfoServiceSoapType;
import jakarta.xml.ws.BindingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
@EnableWebMvc
@ComponentScan({
        "io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller",
        "io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service"
})
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/images/**").addResourceLocations("/images/");
    }

    @Bean
    public CountryInfoServiceSoapType countryInfoService() {
        CountryInfoServiceSoapType port = new CountryInfoService().getCountryInfoServiceSoap();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(JAXWSProperties.CONNECT_TIMEOUT, 5_000);
        requestContext.put(JAXWSProperties.REQUEST_TIMEOUT, 10_000);
        return port;
    }

    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory() {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(RestClient.create())).build();
    }

    @Bean
    public OpenMeteoGeocodingClient openMeteoGeocodingClient(HttpServiceProxyFactory factory) {
        return factory.createClient(OpenMeteoGeocodingClient.class);
    }

    @Bean
    public OpenMeteoForecastClient openMeteoForecastClient(HttpServiceProxyFactory factory) {
        return factory.createClient(OpenMeteoForecastClient.class);
    }

    @Bean
    public OpenMeteoAirQualityClient openMeteoAirQualityClient(HttpServiceProxyFactory factory) {
        return factory.createClient(OpenMeteoAirQualityClient.class);
    }

    @Bean
    public OpenMeteoArchiveClient openMeteoArchiveClient(HttpServiceProxyFactory factory) {
        return factory.createClient(OpenMeteoArchiveClient.class);
    }

}
