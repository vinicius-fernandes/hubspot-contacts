package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.configuration.HubspotAuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AuthorizationService {

    @Autowired
    HubspotAuthProperties hubspotAuthProperties;
    public String getAuthorizationUrl(){
        try {
            StringBuilder url = new StringBuilder(hubspotAuthProperties.getAuthorizationUrl());
            url.append("?client_id=").append(URLEncoder.encode(hubspotAuthProperties.getClientId(), StandardCharsets.UTF_8.name()));
            url.append("&redirect_uri=").append(URLEncoder.encode(hubspotAuthProperties.getAuthRedirectUrl(), StandardCharsets.UTF_8.name()));
            url.append("&scope=oauth");

            return url.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }

    }

    public String exchangeCodeForToken(String code){
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

        ResponseEntity<String> response = restTemplate.exchange(hubspotAuthProperties.getTokenUrl(), HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Tokens recebidos: " + response.getBody());
            return response.getBody();
        } else {
            System.out.println("Erro ao obter tokens: " + response.getStatusCode());
            throw new RuntimeException("Failed to get token");
        }
    }
}
