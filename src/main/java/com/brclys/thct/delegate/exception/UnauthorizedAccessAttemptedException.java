package com.brclys.thct.delegate.exception;

public class UnauthorizedAccessAttemptedException extends RuntimeException {
    public UnauthorizedAccessAttemptedException(String message) {
        super(message);
    }
    public UnauthorizedAccessAttemptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
