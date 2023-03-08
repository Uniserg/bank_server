package com.serguni.services;

import com.serguni.models.DebitCard;
import com.serguni.repositories.DebitCardRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class DebitCardService {

    @Inject
    IndividualService individualService;

    @Inject
    DebitCardRepository debitCardRepository;

    @Inject
    ProductService productService;


    public Stream<DebitCard> getAllByUserSub(String userSub, int skip, int limit) {
        return debitCardRepository.getAllByUserSub(userSub, skip, limit);
    }
}
