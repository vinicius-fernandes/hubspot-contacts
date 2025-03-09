package com.vinfern.hubspot.contacts.dto.webhook;

public record ValidatedWebhook(
        String method,
        String fullUrl,
        String rawBody
) {
    public ValidatedWebhook(WebhookRequest request) {
        this(request.method(), request.fullUrl(), request.rawBody());
    }
}
