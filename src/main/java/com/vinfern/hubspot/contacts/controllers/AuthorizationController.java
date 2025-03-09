package com.vinfern.hubspot.contacts.controllers;

import com.vinfern.hubspot.contacts.dto.auth.AuthResponse;
import com.vinfern.hubspot.contacts.services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    AuthorizationService authorizationService;

    @GetMapping("/url")
    public ResponseEntity<Void> getAuthorizationUrl() {
        var authorizationUrl = authorizationService.getAuthorizationUrl();
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(authorizationUrl))
                .build();
    }

    @GetMapping("/oauth-callback")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse processOAuthCallback(@RequestParam String code
                                             ){
        return authorizationService.exchangeCodeForToken(code);
    }


}
