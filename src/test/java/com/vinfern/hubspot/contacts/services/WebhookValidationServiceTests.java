package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.webhook.ValidatedWebhook;
import com.vinfern.hubspot.contacts.dto.webhook.WebhookRequest;
import com.vinfern.hubspot.contacts.exception.WebhookValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebhookValidationServiceTests {
    private static final String SECRET = "test_secret";
    private static final String METHOD = "POST";
    private static final String URL = "https://test.com/webhook";
    private static final String BODY = "{\"event\": \"test\"}";
    private static final long TIMESTAMP = Instant.now().toEpochMilli();
    private static final String SIGNATURE = "valid_signature";
    @InjectMocks
    private WebhookValidationService webhookValidationService;
    @Mock
    private HubspotProperties hubspotProperties;

    static Stream<Arguments> validWebhookRequests() {
        return Stream.of(
                Arguments.of(new WebhookRequest(METHOD, URL, BODY, SIGNATURE, String.valueOf(TIMESTAMP)), true),
                Arguments.of(new WebhookRequest("GET", "https://example.com/other-webhook", "{\"event\": \"test\"}", SIGNATURE, String.valueOf(TIMESTAMP)), true)
        );
    }

    static Stream<Arguments> missingHeadersRequests() {
        return Stream.of(
                Arguments.of(null, "1631023200000", "INVALID_HEADERS", 400),
                Arguments.of(SIGNATURE, null, "INVALID_HEADERS", 400),
                Arguments.of(null, null, "INVALID_HEADERS", 400)
        );
    }

    static Stream<String> invalidTimestampRequests() {
        return Stream.of("invalid_timestamp", "not_a_number", "e");
    }


    @ParameterizedTest
    @MethodSource("validWebhookRequests")
    void validateRequest_ValidWebhook_ReturnsValidatedWebhook(WebhookRequest request, boolean expectedResult) throws Exception {
        String validSignature = generateValidSignature(request.method(), request.fullUrl(), request.rawBody(), Long.parseLong(request.timestamp()));

        WebhookRequest validRequest = new WebhookRequest(
                request.method(),
                request.fullUrl(),
                request.rawBody(),
                validSignature,
                request.timestamp()
        );

        when(hubspotProperties.getClientSecret()).thenReturn(SECRET);

        ValidatedWebhook result = webhookValidationService.validateRequest(validRequest);

        if (expectedResult) {
            assertNotNull(result);
            assertEquals(validRequest.method(), result.method());
            assertEquals(validRequest.rawBody(), result.rawBody());
            assertEquals(validRequest.fullUrl(), result.fullUrl());
        }
    }

    @ParameterizedTest
    @MethodSource("missingHeadersRequests")
    void validateRequest_MissingHeaders_ThrowsException(String signature, String timestamp, String expectedMessage, int expectedStatus) {
        WebhookRequest request = new WebhookRequest(METHOD, URL, BODY, timestamp, signature);

        WebhookValidationException exception = assertThrows(WebhookValidationException.class, () -> {
            webhookValidationService.validateRequest(request);
        });

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(HttpStatus.valueOf(expectedStatus), exception.getStatusCode());
    }


    @ParameterizedTest
    @MethodSource("invalidTimestampRequests")
    void validateRequest_InvalidTimestampFormat_ThrowsException(String timestamp) {
        WebhookRequest request = new WebhookRequest(METHOD, URL, BODY, timestamp, SIGNATURE);

        WebhookValidationException exception = assertThrows(WebhookValidationException.class, () -> {
            webhookValidationService.validateRequest(request);
        });

        assertEquals("INVALID_TIMESTAMP", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void validateRequest_ExpiredTimestamp_ThrowsException() {
        long expiredTimestamp = Instant.now().toEpochMilli() - 600_000;

        WebhookRequest request = new WebhookRequest(METHOD, URL, BODY, String.valueOf(expiredTimestamp), SIGNATURE);

        WebhookValidationException exception = assertThrows(WebhookValidationException.class, () -> {
            webhookValidationService.validateRequest(request);
        });

        assertEquals("INVALID_TIMESTAMP", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void validateRequest_InvalidSignature_ThrowsException() {
        WebhookRequest request = new WebhookRequest(METHOD, URL, BODY, "wrong_signature", String.valueOf(TIMESTAMP));

        when(hubspotProperties.getClientSecret()).thenReturn(SECRET);


        WebhookValidationException exception = assertThrows(WebhookValidationException.class, () -> {
            webhookValidationService.validateRequest(request);
        });


        assertEquals("Invalid signature", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    private String generateValidSignature(String method, String url, String body, long timestamp)
            throws NoSuchAlgorithmException, InvalidKeyException {

        String dataToSign = method + url + body + timestamp;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);

        byte[] rawHmac = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
