package com.serguni.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyBank {
    private String name;
    private String correspondAccount;
    private String bik;
    private String inn;
    private String kpp;
    private long accountsCount;
}
