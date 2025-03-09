package com.vinfern.hubspot.contacts.controllers;

import com.vinfern.hubspot.contacts.dto.contact.Contact;
import com.vinfern.hubspot.contacts.dto.contact.HubspotContact;
import com.vinfern.hubspot.contacts.services.ContactsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
public class ContactsController {
    @Autowired
    private ContactsService contactsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HubspotContact createContact(@Valid @RequestBody Contact contact, @RequestHeader("Authorization") String authorizationHeader) {

        return contactsService.createContact(contact, authorizationHeader);

    }


}
