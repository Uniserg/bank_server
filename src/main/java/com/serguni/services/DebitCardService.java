package com.serguni.services;

import com.serguni.models.*;
import com.serguni.notifications.TransferNotificationListener;
import com.serguni.repositories.DebitCardRepository;
import com.serguni.repositories.MyBankRepository;

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
    MyBankRepository myBankRepository;

    @Inject
    TransferNotificationListener transferNotificationListener;


    public Stream<DebitCard> getAllByUserSub(String userSub, int skip, int limit) {
        return debitCardRepository.getAllByUserSub(userSub, skip, limit);
    }

    public Account getAccountByNumber(String number) {
        return debitCardRepository.getAccountByNumber(number);
    }


    private AccountRequisites getAccountRequisites(MyBank myBank, Account account) {
        return AccountRequisites
                .builder()
                .number(account.getNumber().toString())
                .isActive(account.isActive())
                .bankName(myBank.getName())
                .bik(myBank.getBik())
                .inn(myBank.getInn())
                .kpp(myBank.getKpp())
                .correspondAccount(myBank.getCorrespondAccount())
                .build();
    }

    public AccountRequisites getAccountRequisitesByNumber(String number) {
        Account account = getAccountByNumber(number);
        MyBank myBank = myBankRepository.getMyBank();

        return getAccountRequisites(myBank, account);
    }

    public Account getAccount(String userSub, String cardNumber) {
        return debitCardRepository.getAccount(userSub, cardNumber);
    }

    public AccountRequisites getAccountRequisites(String userSub, String cardNumber) {
        MyBank myBank = myBankRepository.getMyBank();
        Account account = getAccount(userSub, cardNumber);
        return getAccountRequisites(myBank, account);
    }

    public Stream<Transfer> getAllTransfers(String userSub, String cardNumber, int skip, int limit) {
        if (limit > 100) {
            limit = 100;
        }
        return debitCardRepository.getAllTransfers(userSub, cardNumber, skip, limit);
    }

    public void createTransfer(String userSub, Transfer transfer) {

        if (transfer.getAmount() <= 0) {
            throw new IllegalArgumentException();
        }
        debitCardRepository.createTransfer(userSub, transfer);

        new Thread(() -> {
            var userTo = individualService
                    .getProfileByCardNumber(transfer.getCardNumberTo());

            SocketMessage message = new SocketMessage();
            message.setScope(SocketMessage.Scope.NOTIFICATION);

            TransferNotification transferNotification = TransferNotification
                    .builder()
                    .createdAt(transfer.getCreatedAt())
                    .userSubFrom(userSub)
                    .amount(transfer.getAmount())
                    .cardNumberTo(transfer.getCardNumberTo())
                    .cardNumberFrom4Postfix(transfer.getCardNumberFrom().substring(12))
                    .message(transfer.getMessage())
                    .build();

            message.setBody(transferNotification);

            transferNotificationListener
                    .send(userTo.getSub(), message);
        })
                .start();
    }

    public Stream<String> getAllCardNumbersByUserSub(String userSub) {
        return debitCardRepository.getAllCardNumbersByUserSub(userSub);
    }
}
