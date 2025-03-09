package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.configuration.HubspotAuthProperties;
import com.vinfern.hubspot.contacts.dto.webhook.ValidatedWebhook;
import com.vinfern.hubspot.contacts.dto.webhook.WebhookRequest;
import com.vinfern.hubspot.contacts.exception.WebhookValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

import static com.vinfern.hubspot.contacts.utils.StringUtils.isNullOrEmpty;

@Service
public class WebhookValidationService {
    private static final long MAX_ALLOWED_TIMESTAMP_MS = 300_000;
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final Logger logger = LoggerFactory.getLogger(WebhookValidationService.class);
    @Autowired
    private HubspotAuthProperties hubspotAuthProperties;

    public ValidatedWebhook validateRequest(WebhookRequest request) {

        if (isNullOrEmpty(request.signature()) || isNullOrEmpty(request.timestamp())) {
            throw new WebhookValidationException(
                    "INVALID_HEADERS",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!isValidTimestamp(request.timestamp())) {
            throw new WebhookValidationException(
                    "INVALID_TIMESTAMP",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!isValidSignature(request)) {
            throw new WebhookValidationException(
                    "Invalid signature",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return new ValidatedWebhook(request);
    }

    private boolean isValidTimestamp(String timestampHeader) {
        try {
            long timestamp = Long.parseLong(timestampHeader);

            long currentTime = Instant.now().toEpochMilli();
            logger.info("Current time {}, Hubspot time stamp {}", currentTime, timestamp);

            long timeDifference = Math.abs(currentTime - timestamp);

            logger.info("Time difference {}", timeDifference);
            return timeDifference <= MAX_ALLOWED_TIMESTAMP_MS;

        } catch (NumberFormatException e) {
            logger.info("Invalid timestamp number format");

            return false;
        }
    }


    private boolean isValidSignature(WebhookRequest request) {
        try {
            String dataToSign = request.method()
                    + request.fullUrl()
                    + request.rawBody()
                    + request.timestamp();

            String computedSignature = generateHmacSignature(dataToSign);

            return MessageDigest.isEqual(
                    computedSignature.getBytes(StandardCharsets.UTF_8),
                    request.signature().getBytes(StandardCharsets.UTF_8)
            );

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new WebhookValidationException("Signature validation failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateHmacSignature(String data)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(
                hubspotAuthProperties.getClientSecret().getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );
        mac.init(secretKey);

        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
