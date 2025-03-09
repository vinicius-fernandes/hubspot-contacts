package com.vinfern.hubspot.contacts.dto.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubspotContact(Long id,HubspotContactProperties properties) {
}
