package com.vinfern.hubspot.contacts.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HubspotResponseErrorHandlerTests {
    private final URI testUrl = URI.create("https://api.hubapi.com/crm/v3/objects/contacts");
    private final HttpMethod testMethod = HttpMethod.GET;
    private HubspotResponseErrorHandler errorHandler;
    @Mock
    private ClientHttpResponse mockResponse;

    @BeforeEach
    void setUp() {
        errorHandler = new HubspotResponseErrorHandler();
    }

    @Test
    void testHasError_ClientError() throws IOException {
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        assertTrue(errorHandler.hasError(mockResponse));
    }

    @Test
    void testHasError_ServerError() throws IOException {
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        assertTrue(errorHandler.hasError(mockResponse));
    }

    @Test
    void testHasError_NoError() throws IOException {
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        assertFalse(errorHandler.hasError(mockResponse));
    }

    @Test
    void testHandleError_SuccessfulParsing() throws IOException {
        String responseBody = "{\"message\":\"Bad Request\",\"error\":\"Invalid data\"}";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream(responseBody.getBytes()));
        assertThrows(HubspotResponseErrorException.class, () -> {
            errorHandler.handleError(testUrl, testMethod, mockResponse);
        });
    }

    @Test
    void testHandleError_ParsingFailure() throws IOException {
        String invalidResponseBody = "Invalid JSON";
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream(invalidResponseBody.getBytes()));
        assertThrows(HubspotResponseErrorException.class, () -> {
            errorHandler.handleError(testUrl, testMethod, mockResponse);
        });
    }
}
