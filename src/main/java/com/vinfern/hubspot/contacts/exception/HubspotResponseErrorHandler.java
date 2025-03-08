package com.vinfern.hubspot.contacts.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.dto.HubspotError;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class HubspotResponseErrorHandler implements ResponseErrorHandler {


    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().is4xxClientError() ||
                response.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes());
        HubspotError errorResponse = new HubspotError("","","","");
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            errorResponse = objectMapper.readValue(responseBody, HubspotError.class);

        } catch (Exception e) {
            throw new HubspotResponseErrorException( response.getStatusCode(),errorResponse);
        }

        throw new HubspotResponseErrorException(response.getStatusCode(),errorResponse);

    }
}
