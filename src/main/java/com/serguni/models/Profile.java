package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Profile {
    private String sub;
    private String email;
    private String lastName;
    private String firstName;
    private byte[] avatar;
}
