package com.littlehorizons.erp.exception;

/** Unchecked wrapper for SQL and configuration failures raised in the DAO layer. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
