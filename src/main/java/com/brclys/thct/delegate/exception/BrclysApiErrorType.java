package com.brclys.thct.delegate.exception;

public enum BrclysApiErrorType {
    NOT_FOUND("User or Resource not found"),
    FORBIDDEN("Unauthorized access"),
    BAD_REQUEST("Bad request"),
    DUPLICATE_USER("User already exists"),
    INTERNAL_SERVER_ERROR("Internal server error");


    private final String value;

    BrclysApiErrorType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }


}