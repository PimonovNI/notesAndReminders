package com.example.notesAndReminders.util.exceptions;

import org.springframework.security.core.AuthenticationException;

public class EmailVerifyingException extends AuthenticationException {
    public EmailVerifyingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public EmailVerifyingException(String msg) {
        super(msg);
    }
}
