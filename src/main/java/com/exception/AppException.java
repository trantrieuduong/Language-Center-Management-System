package com.exception;

/**
 * Root of the application exception hierarchy.
 * All domain / application exceptions extend this class.
 */
public abstract class AppException extends RuntimeException {

    private final String userMessage;

    public AppException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }

    public AppException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }

    /** Human-readable message safe to display in UI dialogs. */
    public String getUserMessage() {
        return userMessage;
    }
}
