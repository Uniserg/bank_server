package com.serguni.services;

import com.ibm.icu.text.Transliterator;
import com.serguni.models.*;
import com.serguni.models.requisites.CardNumber;
import com.serguni.models.requisites.CurrentAccount;
import com.serguni.repositories.ProductRequestRepository;
import com.serguni.vars.MyBankVars;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

import static com.serguni.vars.MyBankVars.CYRILLIC_TO_LATIN;

@ApplicationScoped
public class ProductRequestService {
    @Inject
    ProductRequestRepository productRequestRepository;

    @Inject
    ProductService productService;

    @Inject
    IndividualService individualService;

    public ProductRequest create(ProductRequest productRequest) { // TODO: сделать DTO для ProductRequest
        productRequest.setStatus(ProductRequest.ProductRequestStatus.CONFIRM_AWAIT);
        productRequest.setId(productRequestRepository.create(productRequest));
        return productRequest;
    }

    public Stream<ProductRequest> getAllByUserSub(String userSub, int skip, int limit) {
        return productRequestRepository.getAllByUserSub(userSub, skip, limit);
    }

    public void complete(long productRequestId) {
        ProductRequest productRequest = productRequestRepository.getById(productRequestId);

        if (productRequest.getStatus() != ProductRequest.ProductRequestStatus.IN_PROGRESS){
            throw new RuntimeException(); // TODO: сделать ошибку
        }

        Account account = getNewAccount(productRequest.getProductName());
        DebitCard debitCard = getNewDebitCard(productRequest);

        productRequestRepository.complete(productRequestId, account, debitCard);
    }

    public void confirm(long productRequestId) {
        productRequestRepository.confirm(productRequestId);
    }

    private Account getNewAccount(String productName) {
        Account account = new Account(
                new CurrentAccount(CurrentAccount.FirstOrder.INDIVIDUAL,
                        CurrentAccount.SecondOrder.CARD,
                        CurrentAccount.UnitCode.RUR,
                        productService.incrCount(productName),
                        MyBankVars.MY_BANK_BIK),
                0f,
                false
        );

        return account;
    }

    public DebitCard getNewDebitCard(ProductRequest productRequest) {

        Individual individual = individualService.getBySub(productRequest.getUserSub());
        Product product = productService.getByName(productRequest.getProductName());


        if (individual == null) {
            throw new RuntimeException(); // TODO: сделать осмысленную ошибку
        }

        Account newAccount = getNewAccount(productRequest.getProductName());

        CardNumber cardNumber = new CardNumber(
                CardNumber.PaymentCode.MIR,
                MyBankVars.BANK_ID,
                newAccount.getNumber().getAccountNumber()
        );

        String holderName = Transliterator.getInstance(CYRILLIC_TO_LATIN)
                .transliterate(individual.getLastName() + " " + individual.getFirstName());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, product.getPeriod());


        return new DebitCard(
                cardNumber,
                holderName,
                c.getTime(),
                (short) (new Random().nextInt(900) + 100),
                product.getName()
        );
    }
}
