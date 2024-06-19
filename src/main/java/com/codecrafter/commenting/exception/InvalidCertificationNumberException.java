package com.codecrafter.commenting.exception;

public class InvalidCertificationNumberException extends RuntimeException {
    public InvalidCertificationNumberException() {
        super("Invalid certification number provided.");
    }

    public InvalidCertificationNumberException(String message) {
        super(message);
    }
}