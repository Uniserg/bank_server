package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Transfer {
    private Date createdAt;
    private double amount;
    private String sessionSub;
    private String message;
    private String cardNumberFrom;
    private String cardNumberTo;
    private String nameFrom;
    private String nameTo;
}
