package com.example.graph.exception;

public class EmailTakenException extends IllegalArgumentException{
    public EmailTakenException() {
    }

    public EmailTakenException(String s) {
        super(s);
    }

    public EmailTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailTakenException(Throwable cause) {
        super(cause);
    }
}
