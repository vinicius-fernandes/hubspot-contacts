package com.vinfern.hubspot.contacts.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static com.vinfern.hubspot.contacts.utils.StringUtils.isNullOrEmpty;

@Configuration
public class HubspotAuthProperties {

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


    @PostConstruct
    public void validateProperties() {
        if (isNullOrEmpty(clientId) || isNullOrEmpty(clientSecret) ||
                isNullOrEmpty(tokenUrl) || isNullOrEmpty(authRedirectUrl) ||
                isNullOrEmpty(authorizationUrl)) {
            throw new IllegalStateException("One or more required HubSpot properties are missing or empty. Application cannot start.");
        }
    }


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getAuthRedirectUrl() {
        return authRedirectUrl;
    }

    public void setAuthRedirectUrl(String authRedirectUrl) {
        this.authRedirectUrl = authRedirectUrl;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }
}
