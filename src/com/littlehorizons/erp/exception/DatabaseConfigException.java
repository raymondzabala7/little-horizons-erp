package com.littlehorizons.erp.exception;

/**
 * Thrown when the database configuration cannot be loaded or is incomplete.
 */
public class DatabaseConfigException extends RuntimeException {

    public DatabaseConfigException(String message) {
        super(message);
    }

    public DatabaseConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}