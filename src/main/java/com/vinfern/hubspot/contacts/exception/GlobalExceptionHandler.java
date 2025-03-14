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
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApplicationError handleRuntimeException(Exception ex) {
        logger.error("Runtime exception, details: {}", ex.getMessage());

        return new ApplicationError(
                "Unexpected server error",
                "UNEXPECTED_SERVER_ERROR",
                List.of()
        );
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApplicationError handleParseException(ParseException ex) {

        return new ApplicationError(
                "Parsing error",
                "PARSING_ERROR",
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApplicationError handleInvalidAccessTokenException(InvalidAccessTokenException ex) {

        return new ApplicationError(
                "Invalid access token",
                "INVALID_ACCESS_TOKEN",
                List.of(ex.getMessage())
        );
    }
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApplicationError handleNoResourceFoundException(NoResourceFoundException ex) {

        return new ApplicationError(
                "The desired resource is not available",
                "ROUTE_NOT_FOUND",
                List.of(ex.getMessage())
        );
    }
}
