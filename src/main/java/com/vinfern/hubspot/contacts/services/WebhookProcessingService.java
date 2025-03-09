package com.vinfern.hubspot.contacts.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.dto.webhook.WebhookEvent;
import com.vinfern.hubspot.contacts.dto.webhook.WebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WebhookProcessingService {
    private final Logger logger = LoggerFactory.getLogger(WebhookProcessingService.class);

    @Autowired
    private WebhookValidationService webhookValidationService;

    @Autowired
    private ObjectMapper objectMapper;

    public void processRequest(WebhookRequest webhookRequest) {

        logger.info("Request to be processed {}", webhookRequest);
        var validationResult = webhookValidationService.validateRequest(webhookRequest);

        logger.info("Valid data received {}", validationResult.rawBody());


        try {
            var events = objectMapper.readValue(validationResult.rawBody(), new TypeReference<List<WebhookEvent>>() {
            });

            var contactCreation = events.stream().filter(f -> Objects.equals(f.subscriptionType(), "contact.creation")).toList();

            logger.info("Contact created events {}", contactCreation);

        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("Failed to parse events");
        }
    }
}
