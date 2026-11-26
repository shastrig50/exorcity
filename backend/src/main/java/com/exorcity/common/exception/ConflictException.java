package com.exorcity.common.exception;

/** The request is valid but conflicts with current state (e.g. spot already cleaned). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
