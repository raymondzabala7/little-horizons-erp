package com.littlehorizons.erp.exception;

/** Thrown by services when a business rule is violated (duplicate, capacity exceeded, ...). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
