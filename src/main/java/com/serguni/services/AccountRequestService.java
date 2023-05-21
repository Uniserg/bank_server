package com.serguni.services;

import com.serguni.models.ProductOrder;
import com.serguni.repositories.AccountRequestRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AccountRequestService {

    @Inject
    AccountRequestRepository accountRequestRepository;

    public void create(ProductOrder productOrder) {
        accountRequestRepository.create(productOrder);
    }

    public void confirm(long accountRequestId) {
        accountRequestRepository.confirm(accountRequestId);
    }
}
