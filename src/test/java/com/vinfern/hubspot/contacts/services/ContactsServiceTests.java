package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.clients.HubspotContactsClient;
import com.vinfern.hubspot.contacts.dto.contact.Contact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContactProperties;
import com.vinfern.hubspot.contacts.exception.InvalidAccessTokenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactsServiceTests {
    @Mock
    private HubspotContactsClient hubspotContactsClient;

    @InjectMocks
    private ContactsService contactsService;

    private final String authorizationHeader = "Bearer validToken123";
    private final Contact contact = new Contact("email@example.com", "John", "Doe");
    private final HubspotContact hubspotContact = new HubspotContact(12345L, new HubspotContactProperties("email@example.com", "Doe", "John"));

    @Test
    void testCreateContact_Success() {
        when(hubspotContactsClient.createContact(eq(contact), eq("validToken123"))).thenReturn(hubspotContact);

        var result = contactsService.createContact(contact, authorizationHeader);

        assertNotNull(result);
        assertEquals(12345L, result.id());
        assertEquals("email@example.com", result.properties().email());
        assertEquals("John", result.properties().firstname());
        assertEquals("Doe", result.properties().lastname());

        verify(hubspotContactsClient).createContact(eq(contact), eq("validToken123"));
    }

    @Test
    void testCreateContact_InvalidAuthorizationHeader() {
        String invalidAuthorizationHeader = "Bearer ";

        assertThrows(InvalidAccessTokenException.class, () -> {
            contactsService.createContact(contact, invalidAuthorizationHeader);
        });
    }

    @Test
    void testCreateContact_EmptyAuthorizationHeader() {
        String emptyAuthorizationHeader = "";

        assertThrows(InvalidAccessTokenException.class, () -> {
            contactsService.createContact(contact, emptyAuthorizationHeader);
        });
    }

}
