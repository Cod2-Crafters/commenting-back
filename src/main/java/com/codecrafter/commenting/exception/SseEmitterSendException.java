package com.codecrafter.commenting.exception;

public class SseEmitterSendException extends RuntimeException{

    public SseEmitterSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
