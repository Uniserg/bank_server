package com.serguni.exceptions;

public class InvalidRegistrationForm extends RuntimeException {
    public InvalidRegistrationForm(String message) {
        super(message);
    }
}
