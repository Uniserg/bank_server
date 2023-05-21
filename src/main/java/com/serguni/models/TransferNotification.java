package com.serguni.models;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferNotification {
    private Date createdAt;
    private double amount;
    private String message;
    private String userSubFrom;
    private String cardNumberFrom4Postfix;
    private String cardNumberTo;
}
