package com.thoughtworks.elk.container.exception;


public class ElkParseException extends Exception {
    private String message;

    public ElkParseException() {

    }

    public ElkParseException(String message) {
        this.message = message;
    }
}
