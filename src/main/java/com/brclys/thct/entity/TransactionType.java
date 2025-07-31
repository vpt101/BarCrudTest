package com.brclys.thct.entity;

public enum TransactionType {
    DEPOSIT("deposit"),
    WITHDRAWAL("withdrawal"),
    TRANSFER("transfer");
    
    private final String value;
    
    private TransactionType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}