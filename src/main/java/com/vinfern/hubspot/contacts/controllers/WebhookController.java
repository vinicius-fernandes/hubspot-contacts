package com.vinfern.hubspot.contacts.controllers;

import com.vinfern.hubspot.contacts.dto.webhook.WebhookRequest;
import com.vinfern.hubspot.contacts.services.WebhookProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @Autowired
    private WebhookProcessingService webhookProcessingService;

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void webhook(@RequestBody String body,
                        @RequestHeader("X-HubSpot-Signature-v3") String signature,
                        @RequestHeader("X-HubSpot-Request-Timestamp") String timestamp,
                        HttpServletRequest request
    ) {

        String forwardedProto = request.getHeader("X-Forwarded-Proto");

        var requestUrl = request.getRequestURL().toString();
        if ("http".equalsIgnoreCase(request.getProtocol()) && "https".equalsIgnoreCase(forwardedProto))
            requestUrl = requestUrl.replace("http", "https");

        webhookProcessingService.processRequest(new WebhookRequest(
                request.getMethod(),
                requestUrl,
                body,
                signature,
                timestamp
        ));

    }
}
