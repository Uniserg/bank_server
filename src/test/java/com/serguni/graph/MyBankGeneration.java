package com.serguni.graph;

import com.serguni.models.MyBank;
import com.serguni.repositories.MyBankRepository;
import com.serguni.vars.MyBankVars;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

//@QuarkusTest
public class MyBankGeneration {
    @Inject
    MyBankRepository myBankRepository;

//    @Test
    public void createMyBank() {
        MyBank myBank = new MyBank();

        myBank.setBik(MyBankVars.MY_BANK_BIK.toString());
        myBank.setName("My bank");
        myBank.setKpp("302102");
        myBank.setCorrespondAccount("091831903813902379");
        myBank.setAccountsCount(0);

        myBankRepository.create(myBank);
    }
}
