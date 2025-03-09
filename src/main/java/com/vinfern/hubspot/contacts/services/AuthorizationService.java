package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.clients.HubspotAuthClient;
import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.auth.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AuthorizationService {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    @Autowired
    private HubspotProperties hubspotProperties;

    @Autowired
    private HubspotAuthClient hubspotAuthClient;

    public String getAuthorizationUrl() {
        StringBuilder url = new StringBuilder(hubspotProperties.getAuthorizationUrl());
        url.append("?client_id=").append(URLEncoder.encode(hubspotProperties.getClientId(), StandardCharsets.UTF_8));
        url.append("&redirect_uri=").append(URLEncoder.encode(hubspotProperties.getAuthRedirectUrl(), StandardCharsets.UTF_8));
        url.append("&scope=").append(URLEncoder.encode("oauth crm.objects.contacts.write crm.objects.contacts.read", StandardCharsets.UTF_8));

        return url.toString();

    }

    public AuthResponse exchangeCodeForToken(String code) {
        return hubspotAuthClient.exchangeCodeForToken(code);
    }
}
