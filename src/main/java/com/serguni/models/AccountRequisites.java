package com.serguni.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequisites {
    private String number;
    private String bik;
    private String inn;
    private String kpp;
    private String correspondAccount;
    private String bankName;
    private boolean isActive;
}
