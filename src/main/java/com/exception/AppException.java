package com.exception;

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

    public String getUserMessage() {
        return userMessage;
    }
}
