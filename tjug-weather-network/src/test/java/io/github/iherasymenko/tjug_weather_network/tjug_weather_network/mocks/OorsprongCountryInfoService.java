package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.mocks;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockRequest;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockResponse;
import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra.MockVirtualHost;
import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.xml.XmlMapper;

public final class OorsprongCountryInfoService extends MockVirtualHost {

    private static final XmlMapper XML = new XmlMapper();

    public OorsprongCountryInfoService() {
        post("/websamples.countryinfo/CountryInfoService.wso", this::soap);
    }

    @Override
    protected String host() {
        return "webservices.oorsprong.org";
    }

    private MockResponse soap(MockRequest request) {
        // <soap:Envelope><soap:Body><FullCountryInfo><sCountryISOCode>CA</sCountryISOCode>...; Jackson ignores the namespaces
        JsonNode fullCountryInfo = XML.readTree(request.body()).path("Body").path("FullCountryInfo");
        if (fullCountryInfo.isMissingNode()) {
            return MockResponse.notFound();
        }
        String isoCode = fullCountryInfo.path("sCountryISOCode").asString();

        return switch (isoCode) {
            case "CA" -> new MockResponse(200, "text/xml; charset=utf-8", """
                    <?xml version="1.0" encoding="utf-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                      <soap:Body>
                        <m:FullCountryInfoResponse xmlns:m="http://www.oorsprong.org/websamples.countryinfo">
                          <m:FullCountryInfoResult>
                            <m:sISOCode>CA</m:sISOCode>
                            <m:sName>Canada</m:sName>
                            <m:sCapitalCity>Ottawa</m:sCapitalCity>
                            <m:sPhoneCode>1</m:sPhoneCode>
                            <m:sContinentCode>AM</m:sContinentCode>
                            <m:sCurrencyISOCode>CAD</m:sCurrencyISOCode>
                            <m:sCountryFlag>http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Canada.jpg</m:sCountryFlag>
                            <m:Languages>
                              <m:tLanguage>
                                <m:sISOCode>eng</m:sISOCode>
                                <m:sName>English</m:sName>
                              </m:tLanguage>
                              <m:tLanguage>
                                <m:sISOCode>fra</m:sISOCode>
                                <m:sName>French</m:sName>
                              </m:tLanguage>
                            </m:Languages>
                          </m:FullCountryInfoResult>
                        </m:FullCountryInfoResponse>
                      </soap:Body>
                    </soap:Envelope>
                    """);
            case "UA" -> new MockResponse(200, "text/xml; charset=utf-8", """
                    <?xml version="1.0" encoding="utf-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                      <soap:Body>
                        <m:FullCountryInfoResponse xmlns:m="http://www.oorsprong.org/websamples.countryinfo">
                          <m:FullCountryInfoResult>
                            <m:sISOCode>UA</m:sISOCode>
                            <m:sName>Ukraine</m:sName>
                            <m:sCapitalCity>Kiev</m:sCapitalCity>
                            <m:sPhoneCode>380</m:sPhoneCode>
                            <m:sContinentCode>EU</m:sContinentCode>
                            <m:sCurrencyISOCode>UAH</m:sCurrencyISOCode>
                            <m:sCountryFlag>http://www.oorsprong.org/WebSamples.CountryInfo/Flags/Ukraine.jpg</m:sCountryFlag>
                            <m:Languages>
                              <m:tLanguage>
                                <m:sISOCode>ukr</m:sISOCode>
                                <m:sName>Ukrainian</m:sName>
                              </m:tLanguage>
                            </m:Languages>
                          </m:FullCountryInfoResult>
                        </m:FullCountryInfoResponse>
                      </soap:Body>
                    </soap:Envelope>
                    """);
            case "DE" -> new MockResponse(500, "text/xml; charset=utf-8", """
                    <?xml version="1.0" encoding="utf-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                      <soap:Body>
                        <soap:Fault>
                          <faultcode>soap:Server</faultcode>
                          <faultstring>Server was unable to process request.</faultstring>
                        </soap:Fault>
                      </soap:Body>
                    </soap:Envelope>
                    """);
            // Any other code: the real service answers with an empty record
            default -> new MockResponse(200, "text/xml; charset=utf-8", """
                    <?xml version="1.0" encoding="utf-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                      <soap:Body>
                        <m:FullCountryInfoResponse xmlns:m="http://www.oorsprong.org/websamples.countryinfo">
                          <m:FullCountryInfoResult>
                            <m:sISOCode></m:sISOCode>
                            <m:sName>Country not found in the database</m:sName>
                            <m:sCapitalCity></m:sCapitalCity>
                            <m:sPhoneCode></m:sPhoneCode>
                            <m:sContinentCode></m:sContinentCode>
                            <m:sCurrencyISOCode></m:sCurrencyISOCode>
                            <m:sCountryFlag></m:sCountryFlag>
                            <m:Languages>
                            </m:Languages>
                          </m:FullCountryInfoResult>
                        </m:FullCountryInfoResponse>
                      </soap:Body>
                    </soap:Envelope>
                    """);
        };
    }

}
