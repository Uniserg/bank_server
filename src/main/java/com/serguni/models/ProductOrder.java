package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ProductOrder {

    @Getter
    public enum ProductOrderStatus {
        CONFIRM_AWAIT(0),
        IN_PROGRESS(1),
        COMPLETED(2),
        REFUSED(3);

        public static final Map<Integer, ProductOrderStatus> CODES = Map.of(
                0, CONFIRM_AWAIT,
                1, IN_PROGRESS,
                2, COMPLETED,
                3, REFUSED
        );

        private final int code;

        ProductOrderStatus(int code) {
            this.code = code;
        }
    }

    private long id;
    private String userSub;
    private String productName;
    private String address;
    private Date scheduledDate;
    private Date createdAt;
    private ProductOrderStatus status;
}
