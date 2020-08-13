package com.greenlaw110.simplecab;

public class SimpleCabServiceException extends RuntimeException {

    public SimpleCabServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleCabServiceException(String message) {
        super(message);
    }
}
