package com.serguni.models;

import lombok.Data;

@Data
public class RegistrationForm {
    private String lastName;
    private String firstName;
    private String middleName;
    private String phoneNumber;
    private String passport;
    private String inn;
    private String email;
    private String login;
    private String password;
}
