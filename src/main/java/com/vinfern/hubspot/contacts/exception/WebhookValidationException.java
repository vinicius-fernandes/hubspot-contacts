package com.vinfern.hubspot.contacts.exception;

import org.springframework.http.HttpStatus;

public class WebhookValidationException extends RuntimeException{
    private final HttpStatus statusCode;

    public WebhookValidationException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
