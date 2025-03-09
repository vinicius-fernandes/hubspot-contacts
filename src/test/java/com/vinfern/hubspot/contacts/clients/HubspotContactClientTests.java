package com.vinfern.hubspot.contacts.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.contact.Contact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContactProperties;
import com.vinfern.hubspot.contacts.exception.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubspotContactClientTests {

    private static final String CREATE_URL = "https://hubspot.com/contacts";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HubspotProperties hubspotProperties;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private HubspotContactsClient hubspotContactsClient;
    private String accessToken;
    private Contact contact;
    private HubspotContact hubspotContactResponse;

    @BeforeEach
    void setUp() {
        accessToken = "validAccessToken";
        contact = new Contact("email@example.com", "John", "Doe");

        hubspotContactResponse = new HubspotContact(12345L, new HubspotContactProperties("email@example.com", "Doe", "John"));
    }

    @Test
    void testCreateContact_Success() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> properties = new HashMap<>();
        properties.put("email", contact.email());
        properties.put("firstname", contact.firstName());
        properties.put("lastname", contact.lastName());
        requestBody.put("properties", properties);

        ResponseEntity<HubspotContact> responseEntity = new ResponseEntity<>(hubspotContactResponse, HttpStatus.CREATED);
        when(restTemplate.exchange(
                eq(CREATE_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(HubspotContact.class)
        )).thenReturn(responseEntity);

        when(hubspotProperties.getCreateContactUrl()).thenReturn(CREATE_URL);

        HubspotContact result = hubspotContactsClient.createContact(contact, accessToken);

        assertNotNull(result);
        assertEquals(12345L, result.id());
        assertEquals("email@example.com", result.properties().email());
        assertEquals("John", result.properties().firstname());
        assertEquals("Doe", result.properties().lastname());

        verify(restTemplate, times(1)).exchange(
                eq(CREATE_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(HubspotContact.class)
        );
    }

    @Test
    void testCreateContact_ExceptionHandling() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("JSON Parsing error"));

        assertThrows(ParseException.class, () -> hubspotContactsClient.createContact(contact, accessToken));

    }


}
