package com.vinfern.hubspot.contacts.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.contact.Contact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContact;
import com.vinfern.hubspot.contacts.exception.HubspotResponseErrorHandler;
import com.vinfern.hubspot.contacts.exception.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HubspotContactsClient {
    private static final Logger logger = LoggerFactory.getLogger(HubspotContactsClient.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HubspotProperties hubspotProperties;
    @Autowired
    private ObjectMapper objectMapper;

    public HubspotContact createContact(Contact contact, String accessToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> requestBody = new HashMap<>();

        Map<String, String> properties = new HashMap<>();
        properties.put("email", contact.email());
        properties.put("lastname", contact.lastName());
        properties.put("firstname", contact.firstName());

        requestBody.put("properties", properties);

        String jsonBody = null;
        try {
            jsonBody = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            logger.error("Failed to create the contact request body, details {}",e.getMessage());
            throw new ParseException("Failed to create the contact request body");
        }
        HttpEntity<String> contactBody = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<HubspotContact> response = restTemplate.exchange(
                hubspotProperties.getCreateContactUrl(),
                HttpMethod.POST,
                contactBody,
                HubspotContact.class
        );

        return response.getBody();
    }
}
