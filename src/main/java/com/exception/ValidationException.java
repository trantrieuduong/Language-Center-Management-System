package com.exception;

/**
 * Thrown when user input fails validation rules.
 * UI should show a WARNING dialog and allow the user to correct input.
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
