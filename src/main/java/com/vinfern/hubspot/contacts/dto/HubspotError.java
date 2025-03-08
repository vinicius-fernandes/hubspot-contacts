package com.vinfern.hubspot.contacts.dto;

public record HubspotError(String status,String message,String correlationId,String category) {
}
