package com.serguni.models;

import com.serguni.models.requisites.CardNumber;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class Card {
    private CardNumber number;
    private String holderName;
    private Date expirationDate;
    private short cvv;
    private boolean isActive;
    private String productName;

    public Card(CardNumber number, String holderName, Date expirationDate, short cvv, String productName) {
        this.number = number;
        this.holderName = holderName;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.productName = productName;
    }
}
