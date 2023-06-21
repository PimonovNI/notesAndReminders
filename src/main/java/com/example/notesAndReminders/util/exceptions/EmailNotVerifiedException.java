package com.example.notesAndReminders.util.exceptions;

import org.springframework.security.core.AuthenticationException;

public class EmailNotVerifiedException extends AuthenticationException {
    public EmailNotVerifiedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public EmailNotVerifiedException(String msg) {
        super(msg);
    }
}
