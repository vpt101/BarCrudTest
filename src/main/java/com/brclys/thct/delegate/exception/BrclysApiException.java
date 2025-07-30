package com.brclys.thct.delegate.exception;



public class BrclysApiException extends RuntimeException {
    

    public BrclysApiException(BrclysApiErrorType type, String message) {
        super(type.getValue() + ": " + message);
    }
}
