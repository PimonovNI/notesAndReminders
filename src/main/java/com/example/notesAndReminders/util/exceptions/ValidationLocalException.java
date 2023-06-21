package com.example.notesAndReminders.util.exceptions;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationLocalException extends RuntimeException {
    private final Map<String, List<String>> errorsInfo;

    public ValidationLocalException() {
        errorsInfo = new HashMap<>();
    }

    public ValidationLocalException(Map<String, List<String>> errorsInfo) {
        this.errorsInfo = errorsInfo;
    }

    public ValidationLocalException(String message) {
        super(message);
        errorsInfo = new HashMap<>();
    }

    public ValidationLocalException(String message, Map<String, List<String>> errorsInfo) {
        super(message);
        this.errorsInfo = errorsInfo;
    }

    public ValidationLocalException(String message, Throwable cause) {
        super(message, cause);
        errorsInfo = new HashMap<>();
    }

    public ValidationLocalException(String message, Throwable cause, Map<String, List<String>> errorsInfo) {
        super(message, cause);
        this.errorsInfo = errorsInfo;
    }
}
