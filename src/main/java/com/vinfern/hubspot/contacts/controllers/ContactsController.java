package com.vinfern.hubspot.contacts.controllers;

import com.vinfern.hubspot.contacts.dto.contact.Contact;
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
    public void createContact(@Valid @RequestBody Contact contact,@RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            contactsService.createContact(contact,token);

        }
        else{
            System.out.println("No bearer token");
        }
    }

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.OK)
    public void webHook(@RequestBody String body){
        System.out.println(body);
    }
}
