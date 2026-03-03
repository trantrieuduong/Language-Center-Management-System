package com.exception;

/**
 * Thrown when a business rule is violated (e.g. class is full, schedule
 * conflict).
 * UI should show a WARNING dialog.
 */
public class BusinessException extends AppException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
