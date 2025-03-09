package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.configuration.HubspotAuthProperties;
import com.vinfern.hubspot.contacts.dto.auth.AuthResponse;
import com.vinfern.hubspot.contacts.exception.HubspotTokenExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AuthorizationService {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    @Autowired
    private HubspotAuthProperties hubspotAuthProperties;

    public String getAuthorizationUrl() {
        try {
            StringBuilder url = new StringBuilder(hubspotAuthProperties.getAuthorizationUrl());
            url.append("?client_id=").append(URLEncoder.encode(hubspotAuthProperties.getClientId(), StandardCharsets.UTF_8.name()));
            url.append("&redirect_uri=").append(URLEncoder.encode(hubspotAuthProperties.getAuthRedirectUrl(), StandardCharsets.UTF_8.name()));
            url.append("&scope=").append(URLEncoder.encode("oauth crm.objects.contacts.write crm.objects.contacts.read",StandardCharsets.UTF_8.name()));

            return url.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }

    }

    public AuthResponse exchangeCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", hubspotAuthProperties.getClientId());
        formData.add("client_secret", hubspotAuthProperties.getClientSecret());
        formData.add("redirect_uri", hubspotAuthProperties.getAuthRedirectUrl());
        formData.add("code", code);
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        try {
            ResponseEntity<AuthResponse> response = restTemplate.exchange(hubspotAuthProperties.getTokenUrl(), HttpMethod.POST, requestEntity, AuthResponse.class);
            logger.info("Successfully retrieved the token");
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new HubspotTokenExchangeException(ex.getResponseBodyAsString());
        } catch (RuntimeException ex) {
            throw new HubspotTokenExchangeException("Unexpected error while exchanging the code for a token");
        }

    }
}
