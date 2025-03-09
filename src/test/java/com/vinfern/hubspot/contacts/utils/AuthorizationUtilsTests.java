package com.vinfern.hubspot.contacts.utils;

import com.vinfern.hubspot.contacts.exception.InvalidAccessTokenException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthorizationUtilsTests {

    @Test
    void testRetrieveAccessToken_ValidToken() {
        String authorizationHeader = "Bearer validToken123";
        String result = AuthorizationUtils.retrieveAccessToken(authorizationHeader);
        assertEquals("validToken123", result);
    }

    @Test
    void testRetrieveAccessToken_NoBearerPrefix() {
        String authorizationHeader = "InvalidToken123";
        assertThrows(InvalidAccessTokenException.class, () -> {
            AuthorizationUtils.retrieveAccessToken(authorizationHeader);
        });
    }

    @Test
    void testRetrieveAccessToken_EmptyHeader() {
        String authorizationHeader = "";
        assertThrows(InvalidAccessTokenException.class, () -> {
            AuthorizationUtils.retrieveAccessToken(authorizationHeader);
        });
    }

    @Test
    void testRetrieveAccessToken_NullHeader() {
        assertThrows(InvalidAccessTokenException.class, () -> {
            AuthorizationUtils.retrieveAccessToken(null);
        });
    }

    @Test
    void testRetrieveAccessToken_BearerOnly() {
        String authorizationHeader = "Bearer ";
        assertThrows(InvalidAccessTokenException.class, () -> {
            AuthorizationUtils.retrieveAccessToken(authorizationHeader);
        });
    }
}
