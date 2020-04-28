package com.paymentprocessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ExternalServiceNotifactionConfig {

    @Bean
    public RestTemplate externalServiceRestTemplate() {
        return new RestTemplate();
    }

}
