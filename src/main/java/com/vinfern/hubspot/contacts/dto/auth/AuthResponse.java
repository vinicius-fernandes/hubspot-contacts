package com.vinfern.hubspot.contacts.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthResponse(
        String access_token,
        String token_type,
        int expires_in,
        String refresh_token
) {}
