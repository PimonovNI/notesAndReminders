package com.example.notesAndReminders.util.exceptions;

public class SchedulerCreateException extends RuntimeException {
    public SchedulerCreateException() {
    }

    public SchedulerCreateException(String message) {
        super(message);
    }

    public SchedulerCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulerCreateException(Throwable cause) {
        super(cause);
    }
}
