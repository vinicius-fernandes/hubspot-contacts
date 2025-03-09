package com.vinfern.hubspot.contacts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubspotError(String status,String message,String correlationId,String category) {
}
