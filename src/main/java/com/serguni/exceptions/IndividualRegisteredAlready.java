package com.serguni.exceptions;

public class IndividualRegisteredAlready extends RuntimeException {
    public IndividualRegisteredAlready(String message) {
        super(message);
    }

    public IndividualRegisteredAlready() {
    }
}
