package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotNullException extends ResponseStatusException {
    public NotNullException(HttpStatus status, String reason) {
        super(status, reason);
    }
}