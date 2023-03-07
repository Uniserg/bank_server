package com.serguni.models;

import com.serguni.models.requisites.CurrentAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Account {
    private CurrentAccount number;
    private float balance;
    private boolean isActive;
}
