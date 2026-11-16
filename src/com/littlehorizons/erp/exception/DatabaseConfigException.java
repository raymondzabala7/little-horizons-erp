package com.littlehorizons.erp.exception;

/** Thrown when config.properties is missing or invalid. */
public class DatabaseConfigException extends Exception {
    public DatabaseConfigException(String message) { super(message); }
    public DatabaseConfigException(String message, Throwable cause) { super(message, cause); }
}
