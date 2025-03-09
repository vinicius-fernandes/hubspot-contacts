package com.vinfern.hubspot.contacts.clients;

import com.vinfern.hubspot.contacts.configuration.HubspotProperties;
import com.vinfern.hubspot.contacts.dto.auth.AuthResponse;
import com.vinfern.hubspot.contacts.exception.HubspotTokenExchangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubspotAuthClientTests {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HubspotProperties hubspotProperties;

    @InjectMocks
    private HubspotAuthClient hubspotAuthClient;

    @BeforeEach
    void setUp() {
        when(hubspotProperties.getTokenUrl()).thenReturn("https://api.hubspot.com/oauth/v1/token");
        when(hubspotProperties.getClientId()).thenReturn("test-client-id");
        when(hubspotProperties.getClientSecret()).thenReturn("test-client-secret");
        when(hubspotProperties.getAuthRedirectUrl()).thenReturn("https://redirect.url");
    }

    @Test
    void testExchangeCodeForToken_Success() {
        MultiValueMap<String, String> expectedFormData = new LinkedMultiValueMap<>();
        expectedFormData.add("grant_type", "authorization_code");
        expectedFormData.add("client_id", "test-client-id");
        expectedFormData.add("client_secret", "test-client-secret");
        expectedFormData.add("redirect_uri", "https://redirect.url");
        expectedFormData.add("code", "valid-code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(expectedFormData, headers);

        AuthResponse mockResponse = new AuthResponse("mockAccessToken", "Bearer", 3600, "mockRefreshToken");
        ResponseEntity<AuthResponse> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.hubspot.com/oauth/v1/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(AuthResponse.class)
        )).thenReturn(mockResponseEntity);

        AuthResponse response = hubspotAuthClient.exchangeCodeForToken("valid-code");

        assertNotNull(response);
        assertEquals("mockAccessToken", response.access_token());
        assertEquals("Bearer", response.token_type());
        assertEquals(3600, response.expires_in());
        assertEquals("mockRefreshToken", response.refresh_token());

        verify(restTemplate, times(1)).exchange(
                eq("https://api.hubspot.com/oauth/v1/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(AuthResponse.class)
        );
    }

    @Test
    void testExchangeCodeForToken_Failure() {

        var exceptionMock = mock(HttpClientErrorException.class);
        when(exceptionMock.getResponseBodyAsString()).thenReturn("Invalid Code");

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(AuthResponse.class)
        )).thenThrow(exceptionMock);

        HubspotTokenExchangeException exception = assertThrows(HubspotTokenExchangeException.class, () -> {
            hubspotAuthClient.exchangeCodeForToken("invalid-code");
        });

        assertEquals("Invalid Code", exception.getMessage());

        verify(restTemplate, times(1)).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(AuthResponse.class)
        );
    }

}
