package com.vinfern.hubspot.contacts.dto.webhook;

public record WebhookRequest(
        String method,
        String fullUrl,
        String rawBody,
        String signature,
        String timestamp
) {
}
