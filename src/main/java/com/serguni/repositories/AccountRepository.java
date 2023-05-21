package com.serguni.repositories;

import com.serguni.models.Account;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepository extends AbstractRepository {
    public void create(Account account) {
        gd.g().addV("Account")
                .property("number", account.getNumber().toString())
                .property("balance", account.getBalance())
                .property("isActive", account.isActive());
    }
}
