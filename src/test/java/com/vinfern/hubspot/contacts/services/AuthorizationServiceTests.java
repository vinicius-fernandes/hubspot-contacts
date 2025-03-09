package com.vinfern.hubspot.contacts.services;

import com.vinfern.hubspot.contacts.clients.HubspotAuthClient;
import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.auth.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTests {
    @Mock
    private HubspotProperties hubspotProperties; // Mocking properties

    @Mock
    private HubspotAuthClient hubspotAuthClient; // Mocking API client

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void testGetAuthorizationUrl() {
        when(hubspotProperties.getAuthorizationUrl()).thenReturn("https://auth.hubspot.com");
        when(hubspotProperties.getClientId()).thenReturn("test-client-id");
        when(hubspotProperties.getAuthRedirectUrl()).thenReturn("https://redirect.url");

        String result = authorizationService.getAuthorizationUrl();

        StringBuilder expectedUrl = new StringBuilder("https://auth.hubspot.com");
        expectedUrl.append("?client_id=").append(URLEncoder.encode("test-client-id", StandardCharsets.UTF_8));
        expectedUrl.append("&redirect_uri=").append(URLEncoder.encode("https://redirect.url", StandardCharsets.UTF_8));
        expectedUrl.append("&scope=").append(URLEncoder.encode("oauth crm.objects.contacts.write crm.objects.contacts.read", StandardCharsets.UTF_8));

        assertEquals(expectedUrl.toString(), result);
    }

    @Test
    void testExchangeCodeForToken() {
        AuthResponse mockResponse = new AuthResponse("mockAccessToken", "Bearer", 3600, "mockRefreshToken");
        when(hubspotAuthClient.exchangeCodeForToken("valid-code")).thenReturn(mockResponse);

        AuthResponse response = authorizationService.exchangeCodeForToken("valid-code");

        assertNotNull(response);
        assertEquals("mockAccessToken", response.access_token());
        assertEquals("Bearer", response.token_type());
        assertEquals(3600, response.expires_in());
        assertEquals("mockRefreshToken", response.refresh_token());

        verify(hubspotAuthClient, times(1)).exchangeCodeForToken("valid-code");
    }

}
