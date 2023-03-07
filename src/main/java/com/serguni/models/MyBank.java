package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyBank {
    private String name;
    private String correspondAccount;
    private String bik;
    private String kpp;
    private long accountsCount;
}
