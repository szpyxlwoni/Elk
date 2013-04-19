package com.thoughtworks.elk.container.exception;


public class ElkContainerException extends Exception {
    private String messgae;

    public ElkContainerException(String message) {
        this.messgae = message;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
