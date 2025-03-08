package com.vinfern.hubspot.contacts.dto;


import java.util.List;

public record ApplicationError(String message, String error, long timestamp, List<String> details) {
    public ApplicationError( String message, String error, List<String> details) {
        this(message, error, System.currentTimeMillis(), details);
    }
}
