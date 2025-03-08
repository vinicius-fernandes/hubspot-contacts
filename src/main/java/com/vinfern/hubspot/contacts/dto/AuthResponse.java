package com.vinfern.hubspot.contacts.dto;

public record AuthResponse(
        String access_token,
        String token_type,
        int expires_in,
        String refresh_token,
        String scope
) {}
