package com.serguni.exceptions;


public class RegistrationFailed extends RuntimeException {
    public RegistrationFailed() {
    }

    public RegistrationFailed(String message) {
        super(message);
    }
}
