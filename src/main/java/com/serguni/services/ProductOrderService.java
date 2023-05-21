package com.serguni.services;

import com.ibm.icu.text.Transliterator;
import com.serguni.models.*;
import com.serguni.models.requisites.CardNumber;
import com.serguni.models.requisites.CurrentAccount;
import com.serguni.repositories.DebitCardRepository;
import com.serguni.repositories.ProductOrderRepository;
import com.serguni.vars.MyBankVars;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Stream;

import static com.serguni.vars.MyBankVars.CYRILLIC_TO_LATIN;

@ApplicationScoped
public class ProductOrderService {
    @Inject
    ProductOrderRepository productOrderRepository;

    @Inject
    DebitCardRepository debitCardRepository;

    @Inject
    ProductService productService;

    @Inject
    IndividualService individualService;

    public ProductOrder create(ProductOrder productOrder) { // TODO: сделать DTO для ProductOrder
        productOrder.setStatus(ProductOrder.ProductOrderStatus.CONFIRM_AWAIT);
        productOrder.setId(productOrderRepository.create(productOrder));
        return productOrder;
    }

    public Stream<ProductOrder> getAllByUserSub(String userSub, int skip, int limit) {
        return productOrderRepository.getAllByUserSub(userSub, skip, limit);
    }

    public void complete(long productOrderId) {
        ProductOrder productOrder = productOrderRepository.getById(productOrderId);

        if (productOrder.getStatus() != ProductOrder.ProductOrderStatus.IN_PROGRESS){
            throw new RuntimeException(); // TODO: сделать ошибку
        }
        productOrderRepository.complete(productOrderId);
    }

    public void confirm(long productOrderId) {
        ProductOrder productOrder = productOrderRepository.getById(productOrderId);

        if (productOrder.getStatus() != ProductOrder.ProductOrderStatus.CONFIRM_AWAIT) {
            throw new RuntimeException(); // TODO: сделать ошибку
        }

        Product product = productService.getByName(productOrder.getProductName());

        if (product == null) {
            throw  new RuntimeException(); // TODO: сделать ошибку
        }

        Individual individual = individualService.getBySub(productOrder.getUserSub());

        if (individual == null) {
            throw new RuntimeException(); // TODO: сделать осмысленную ошибку
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, product.getPeriod());
        Date expirationDate = c.getTime();

        Account account = getNewAccount(productOrder.getProductName());
        DebitCard debitCard = getNewDebitCard(individual, productOrder, expirationDate);

        long cardId;

        try {
            System.out.println("НАЙДЕНА СВОБОДНАЯ КАРТА");
            cardId = debitCardRepository.reserveFreeCard(
                    getHolderName(individual.getLastName(), individual.getFirstName()),
                    expirationDate
            );
        } catch (NoSuchElementException e) {
            System.out.println("ОШИБКА - НЕ НАЙДЕНА FREE КАРТА");
            cardId = debitCardRepository.createNewCard(productOrderId, account, debitCard);
        }

        debitCardRepository.createReserveEdge(productOrderId, cardId);
        productOrderRepository.confirm(productOrderId);
    }

    public void refuse(long productOrderId) {
        productOrderRepository.refuse(productOrderId);
    }

    private Account getNewAccount(String productName) {

        Account account = new Account(
                new CurrentAccount(CurrentAccount.FirstOrder.INDIVIDUAL,
                        CurrentAccount.SecondOrder.CARD,
                        CurrentAccount.UnitCode.RUR,
                        productService.incrCount(productName),
                        MyBankVars.MY_BANK_BIK),
                0,
                false
        );

        return account;
    }

    public String getHolderName(String lastName, String firstName) {
        return Transliterator.getInstance(CYRILLIC_TO_LATIN)
                .transliterate(lastName + " " + firstName);
    }

    public DebitCard getNewDebitCard(Individual individual, ProductOrder productOrder, Date expirationDate) {
        Product product = productService.getByName(productOrder.getProductName());


        Account newAccount = getNewAccount(productOrder.getProductName());

        CardNumber cardNumber = new CardNumber(
                CardNumber.PaymentCode.MIR,
                MyBankVars.BANK_ID,
                newAccount.getNumber().getAccountNumber()
        );

        return new DebitCard(
                cardNumber,
                getHolderName(individual.getLastName(), individual.getFirstName()),
                expirationDate,
                (short) (new Random().nextInt(900) + 100),
                product.getName()
        );
    }
}
