package com.serguni.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailValidationUtil {
    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    public static boolean isEmailValid(String email) {
        return emailValidator.isValid(email);
    }
}
