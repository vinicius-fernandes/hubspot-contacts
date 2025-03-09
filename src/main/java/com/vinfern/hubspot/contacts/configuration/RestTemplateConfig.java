package com.vinfern.hubspot.contacts.configuration;

import com.vinfern.hubspot.contacts.exception.HubspotResponseErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new HubspotResponseErrorHandler());

        return restTemplate;
    }
}
