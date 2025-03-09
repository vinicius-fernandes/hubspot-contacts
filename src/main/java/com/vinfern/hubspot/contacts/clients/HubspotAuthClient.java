package com.vinfern.hubspot.contacts.clients;

import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.auth.AuthResponse;
import com.vinfern.hubspot.contacts.exception.HubspotTokenExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class HubspotAuthClient {

    private static final Logger logger = LoggerFactory.getLogger(HubspotAuthClient.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HubspotProperties hubspotProperties;

    public AuthResponse exchangeCodeForToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", hubspotProperties.getClientId());
        formData.add("client_secret", hubspotProperties.getClientSecret());
        formData.add("redirect_uri", hubspotProperties.getAuthRedirectUrl());
        formData.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.exchange(
                    hubspotProperties.getTokenUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    AuthResponse.class
            );
            logger.info("Successfully retrieved the token");
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new HubspotTokenExchangeException(ex.getResponseBodyAsString());
        } catch (RuntimeException ex) {
            throw new HubspotTokenExchangeException("Unexpected error while exchanging the code for a token");
        }
    }
}
