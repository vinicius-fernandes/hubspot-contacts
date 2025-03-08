package com.vinfern.hubspot.contacts.controllers;

import com.vinfern.hubspot.contacts.dto.AuthResponse;
import com.vinfern.hubspot.contacts.dto.AuthorizationUrl;
import com.vinfern.hubspot.contacts.services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    AuthorizationService authorizationService;

    @GetMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public AuthorizationUrl getAuthorizationUrl(){
        return authorizationService.getAuthorizationUrl();
    }

    @GetMapping("/oauth-callback")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse processOAuthCallback(@RequestParam String code){
        return authorizationService.exchangeCodeForToken(code);
    }
}
