package com.vinfern.hubspot.contacts.exception;

import com.vinfern.hubspot.contacts.dto.ApplicationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApplicationError handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorDetails = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ApplicationError(
                "The supplied arguments are invalid",
                "INVALID_ARGUMENTS_ERROR",
                errorDetails
        );
    }


    @ExceptionHandler(HubspotResponseErrorException.class)
    public ResponseEntity<ApplicationError> handleHubspotResponseErrorException(HubspotResponseErrorException ex) {
        var hubspotError = ex.getHubspotError();
        var error = new ApplicationError(
                hubspotError.message(),
                hubspotError.category(),
                List.of(hubspotError.correlationId(), hubspotError.status())
        );
        return ResponseEntity.status(ex.getStatusCode()).body(
                error
        );
    }

    @ExceptionHandler(WebhookValidationException.class)
    public ResponseEntity<ApplicationError> handleWebhookValidationException(WebhookValidationException ex) {

        logger.error("Webhook exception, details: {}", ex.getMessage());

        var error = new ApplicationError(
                ex.getMessage(),
                "WEBHOOK_VALIDATION_ERROR",
                List.of()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(
                error
        );
    }

    @ExceptionHandler(HubspotTokenExchangeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApplicationError handleHubspotTokenExchangeException(HubspotTokenExchangeException ex) {
        logger.error("Hubspot token exchange exception, details: {}", ex.getMessage());
        return new ApplicationError(
                "It was not possible to perform the token exchange",
                "HUBSPOT_TOKEN_EXCHANGE_ERROR",
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApplicationError handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception, details: {}", ex.getMessage());

        return new ApplicationError(
                "Unexpected server error",
                "UNEXPECTED_SERVER_ERROR",
                List.of()
        );
    }
}
