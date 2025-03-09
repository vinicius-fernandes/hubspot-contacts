package com.vinfern.hubspot.contacts.configuration;

import com.vinfern.hubspot.contacts.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class HubspotProperties {

    @Value("${hubspot.client.id}")
    private String clientId;

    @Value("${hubspot.client.secret}")
    private String clientSecret;

    @Value("${hubspot.token.url}")
    private String tokenUrl;

    @Value("${hubspot.auth.redirect.url}")
    private String authRedirectUrl;

    @Value("${hubspot.auth.authorization.url}")
    private String authorizationUrl;

    @Value("${hubspot.contacts.create.url}")
    private String createContactUrl;


    @PostConstruct
    public void validateProperties() {
        if (hasAnyNullOrEmptyProperty()) {
            throw new IllegalStateException("One or more required HubSpot properties are missing or empty. Application cannot start.");
        }
    }

    private boolean hasAnyNullOrEmptyProperty() {
        return Stream.of(clientId, clientSecret, tokenUrl, authRedirectUrl, authorizationUrl, createContactUrl)
                .anyMatch(StringUtils::isNullOrEmpty);
    }


    public String getClientId() {
        return clientId;
    }


    public String getClientSecret() {
        return clientSecret;
    }


    public String getTokenUrl() {
        return tokenUrl;
    }


    public String getAuthRedirectUrl() {
        return authRedirectUrl;
    }


    public String getAuthorizationUrl() {
        return authorizationUrl;
    }


    public String getCreateContactUrl() {
        return createContactUrl;
    }
}
