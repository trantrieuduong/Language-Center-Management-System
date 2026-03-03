package com.exception;

/**
 * Thrown when an infrastructure / DB error occurs.
 * UI should show an ERROR dialog and log the full stack trace.
 */
public class SystemException extends AppException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
