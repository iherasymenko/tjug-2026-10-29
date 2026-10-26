package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.countryinfo.CountryInfoServiceSoapType;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.countryinfo.TCountryInfo;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.client.countryinfo.TLanguage;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.City;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.domain.CountryProfile;
import jakarta.xml.ws.WebServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CountryService {

    private static final Log log = LogFactory.getLog(CountryService.class);

    private static final Map<String, String> CAPITAL_CITY_SPELLINGS = Map.of(
            "Kiev", "Kyiv"
    );

    private final CountryInfoServiceSoapType countryInfoService;

    public CountryService(CountryInfoServiceSoapType countryInfoService) {
        this.countryInfoService = countryInfoService;
    }

    public Optional<CountryProfile> countryProfile(City city) {
        if (city.countryCode() == null || city.countryCode().isBlank()) {
            return Optional.empty();
        }
        try {
            TCountryInfo info = countryInfoService.fullCountryInfo(city.countryCode());
            // The service answers unknown codes with an empty record named "Country not found in the database"
            if (info == null || info.getSISOCode() == null || info.getSISOCode().isEmpty()) {
                return Optional.empty();
            }
            List<String> languages = new ArrayList<>();
            if (info.getLanguages() != null) {
                for (TLanguage language : info.getLanguages().getTLanguage()) {
                    languages.add(language.getSName());
                }
            }
            String capital = CAPITAL_CITY_SPELLINGS.getOrDefault(info.getSCapitalCity(), info.getSCapitalCity());
            return Optional.of(new CountryProfile(info.getSName(), capital, info.getSCurrencyISOCode(),
                    info.getSPhoneCode(), info.getSCountryFlag(), languages));
        } catch (WebServiceException e) {
            log.warn("Country information unavailable for " + city.countryCode(), e);
            return Optional.empty();
        }
    }

}
