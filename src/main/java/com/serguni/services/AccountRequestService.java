package com.serguni.services;

import com.serguni.models.ProductRequest;
import com.serguni.repositories.AccountRequestRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AccountRequestService {

    @Inject
    AccountRequestRepository accountRequestRepository;

    public void create(ProductRequest productRequest) {
        accountRequestRepository.create(productRequest);
    }

    public void confirm(long accountRequestId) {
        accountRequestRepository.confirm(accountRequestId);
    }
}
