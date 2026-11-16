package com.littlehorizons.erp.exception;

/** Thrown when standard input is closed (end of file), so the application can exit cleanly. */
public class InputClosedException extends RuntimeException {
    public InputClosedException() { super("Standard input closed"); }
}
