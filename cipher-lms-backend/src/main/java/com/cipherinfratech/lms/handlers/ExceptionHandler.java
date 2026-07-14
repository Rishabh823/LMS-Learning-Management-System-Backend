package com.cipherinfratech.lms.handlers;

public class ExceptionHandler extends RuntimeException{
    public ExceptionHandler(String message) {
        super(message);
    }
    public ExceptionHandler() {
        super("Something went wrong");
    }
}
