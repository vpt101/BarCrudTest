package com.brclys.thct.delegate.exception;


import lombok.Getter;

@Getter
public class BrclysApiException extends RuntimeException {

    private final  BrclysApiErrorType barclysApiErrorType;
    public BrclysApiException(BrclysApiErrorType type, String message) {
        super(type.getValue() + ": " + message);
        barclysApiErrorType = type;
    }
}
