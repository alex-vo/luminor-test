package com.paymentprocessor.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("application.clients")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ClientProperties {
    List<String> usernames;
}
