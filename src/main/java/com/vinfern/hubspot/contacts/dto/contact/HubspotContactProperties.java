package com.vinfern.hubspot.contacts.dto.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubspotContactProperties(String email,String lastname,String firstname) {
}
