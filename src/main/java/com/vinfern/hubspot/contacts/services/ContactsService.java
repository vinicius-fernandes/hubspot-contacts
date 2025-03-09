package com.vinfern.hubspot.contacts.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.contact.Contact;
import com.vinfern.hubspot.contacts.exception.HubspotResponseErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContactsService {
    @Autowired
    private HubspotProperties hubspotProperties;

    @Autowired
    private ObjectMapper objectMapper;



    public void createContact(Contact contact, String token) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new HubspotResponseErrorHandler());
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + token);
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
            e.printStackTrace();
        }
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.hubapi.com/crm/v3/objects/contacts",
                HttpMethod.POST,
                entity,
                String.class
        );
        System.out.println("Response: " + response.getBody());


    }
}
