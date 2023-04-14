package com.serguni.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
