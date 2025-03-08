package com.vinfern.hubspot.contacts.exception;

import com.vinfern.hubspot.contacts.dto.ApplicationError;
import com.vinfern.hubspot.contacts.services.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(HubspotTokenExchangeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApplicationError handleHubspotTokenExchangeException(HubspotTokenExchangeException ex) {
        logger.error("Hubspot token exchange exception, details: {}",ex.getMessage());
        return new ApplicationError(
                LocalDateTime.now(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApplicationError handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception, details: {}",ex.getMessage());

        return new ApplicationError(
                LocalDateTime.now(),
                "Unexpected server error"
        );
    }
}
