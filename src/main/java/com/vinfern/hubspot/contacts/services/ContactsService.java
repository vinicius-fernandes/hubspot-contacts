package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.clients.HubspotContactsClient;
import com.vinfern.hubspot.contacts.dto.contact.Contact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.vinfern.hubspot.contacts.utils.AuthorizationUtils.retrieveAccessToken;

@Service
public class ContactsService {
    private static final Logger logger = LoggerFactory.getLogger(ContactsService.class);

    @Autowired
    private HubspotContactsClient hubspotContactsClient;

    public HubspotContact createContact(Contact contact, String authorizationHeader) {


        String token = retrieveAccessToken(authorizationHeader);

        var createdContact = hubspotContactsClient.createContact(contact, token);
        logger.info("Created contact {}", createdContact);

        return createdContact;

    }
}
