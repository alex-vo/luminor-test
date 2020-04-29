package com.paymentprocessor.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paymentprocessor.service.info.RequestInfo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RabbitListener
@Slf4j
@RequiredArgsConstructor
public class CountryLoggingService {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class CountryInfo {
        @JsonProperty("country_name")
        String countryName;
    }

    public static final String URL = "https://freegeoip.app/json/";

    private final RestTemplate externalServiceRestTemplate;

    @RabbitHandler
    public void handleMessage(RequestInfo requestInfo) {
        String country = resolveCountry(requestInfo);
        if (country == null) {
            return;
        }

        log.info(String.format("Call %s was made from %s", requestInfo, country));
    }

    private String resolveCountry(RequestInfo requestInfo) {
        try {
            CountryInfo countryInfo = externalServiceRestTemplate.getForObject(URL + requestInfo.getIp(), CountryInfo.class);
            return countryInfo.getCountryName();
        } catch (Throwable t) {
            log.error(String.format("Failed to lookup country for request %s", requestInfo), t);
        }

        return null;
    }

}
