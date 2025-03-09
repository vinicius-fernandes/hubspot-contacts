package com.vinfern.hubspot.contacts.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.dto.webhook.ValidatedWebhook;
import com.vinfern.hubspot.contacts.dto.webhook.WebhookEvent;
import com.vinfern.hubspot.contacts.dto.webhook.WebhookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookProcessingServiceTests {

    @InjectMocks
    private WebhookProcessingService webhookProcessingService;

    @Mock
    private WebhookValidationService webhookValidationService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void processRequest_SuccessfulValidationAndProcessing() throws Exception {
        String validJson = "[{\"subscriptionType\":\"contact.creation\", \"objectId\": 123}]";

        var validRequest = new WebhookRequest("POST", "https://example.com/webhook", validJson, "valid_signature", "1631023200000");

        ValidatedWebhook validatedWebhook = new ValidatedWebhook(validRequest);
        when(webhookValidationService.validateRequest(validRequest)).thenReturn(validatedWebhook);
        when(objectMapper.readValue(validJson, new TypeReference<List<WebhookEvent>>() {
        })).thenReturn(List.of(new WebhookEvent(
                9072245L,
                100L,
                3295867L,
                49487763L,
                1741479459943L,
                "contact.creation",
                0,
                123L,
                "CRM",
                "NEW"
        )));

        webhookProcessingService.processRequest(validRequest);

        verify(webhookValidationService).validateRequest(validRequest);
    }
}
