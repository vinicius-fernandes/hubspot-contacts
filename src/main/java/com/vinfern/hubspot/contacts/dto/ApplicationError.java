package com.vinfern.hubspot.contacts.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApplicationError(LocalDateTime timeStamp,String message) {
}
