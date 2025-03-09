package com.vinfern.hubspot.contacts.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEvent(
        Long appId,
        Long eventId,
        Long subscriptionId,
        Long portalId,
        Long occurredAt,
        String subscriptionType,
        Integer attemptNumber,
        Long objectId,
        String changeSource,
        String changeFlag
) {
}
