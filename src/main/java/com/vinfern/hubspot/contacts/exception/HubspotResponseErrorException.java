package com.vinfern.hubspot.contacts.exception;

import com.vinfern.hubspot.contacts.dto.HubspotError;
import org.springframework.http.HttpStatusCode;

public class HubspotResponseErrorException extends RuntimeException{

    private final HttpStatusCode statusCode;
    private final HubspotError hubspotError;
    public HubspotResponseErrorException(HttpStatusCode statusCode, HubspotError hubspotError) {
        this.statusCode = statusCode;
        this.hubspotError = hubspotError;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public HubspotError getHubspotError() {
        return hubspotError;
    }
}
