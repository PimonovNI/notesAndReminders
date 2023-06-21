package com.example.notesAndReminders.util.exceptions;

import org.springframework.security.core.AuthenticationException;

public class IdNotFoundException extends AuthenticationException {
    public IdNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IdNotFoundException(String msg) {
        super(msg);
    }
}
