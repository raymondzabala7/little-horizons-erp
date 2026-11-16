package com.littlehorizons.erp.exception;

/** Thrown when the user types the cancel keyword while a prompt is waiting for input. */
public class InputCancelledException extends RuntimeException {
    public InputCancelledException() { super("Input cancelled by the user"); }
}
