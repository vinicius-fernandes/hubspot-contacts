package com.vinfern.hubspot.contacts.utils;

import com.vinfern.hubspot.contacts.exception.InvalidAccessTokenException;

import static com.vinfern.hubspot.contacts.utils.StringUtils.isNullOrEmpty;

public class AuthorizationUtils {

    public static String retrieveAccessToken(String authorizationHeader) {
        if (isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidAccessTokenException();
        }
        var token = authorizationHeader.substring(7);

        if (isNullOrEmpty(token)) {
            throw new InvalidAccessTokenException();
        }
        return token;
    }
}
