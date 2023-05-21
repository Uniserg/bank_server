package com.serguni.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.serguni.models.requisites.CardNumber;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DebitCard {
    private CardNumber number;
    private String holderName;
    private Date expirationDate;
    private short cvv;
    private boolean isActive;
    private String productName;

    private double balance;

    public DebitCard() {
    }

    public DebitCard(CardNumber number, String holderName, Date expirationDate, short cvv, String productName) {
        this.number = number;
        this.holderName = holderName;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.productName = productName;
    }

    @JsonSetter
    public void setNumber(String numberStr) {

        var paymentCode = Integer.parseInt(numberStr.substring(0, 1));

        var bankId = Integer.parseInt(numberStr.substring(1, 6));

        var accountId = Long.parseLong(numberStr.substring(6, 15));

        var controlSum = Byte.parseByte(numberStr.substring(15, 16));

        CardNumber cardNumber = new CardNumber(CardNumber.PaymentCode.CODES.get(paymentCode), bankId, accountId);

        if (cardNumber.getControlSum() != controlSum) {
            throw new RuntimeException();
        }

        this.number = cardNumber;
    }

    @JsonGetter("number")
    public String getNumber() {
        return number.toString();
    }
}
