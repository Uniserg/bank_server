package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Individual extends Profile {
    private String middleName;
    private String passport;
    private String phoneNumber;
    private String inn;
}
