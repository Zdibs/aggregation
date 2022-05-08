package com.aggregation.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    private String hostname;
    private String port;
    private String trackEndpoint;
    private String pricingEndpoint;
    private String shipmentsEndpoint;
}
