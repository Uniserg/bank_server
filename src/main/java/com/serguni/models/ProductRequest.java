package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ProductRequest {

    @Getter
    public enum ProductRequestStatus {
        CONFIRM_AWAIT(0),
        IN_PROGRESS(1),
        COMPLETED(2);

        public static final Map<Integer, ProductRequestStatus> CODES = Map.of(
                0, CONFIRM_AWAIT,
                1, IN_PROGRESS,
                2, COMPLETED
        );

        private final int code;

        ProductRequestStatus(int code) {
            this.code = code;
        }
    }

    private long id;
    private String userSub;
    private String productName;
    private String address;
    private Date scheduledDate;
    private ProductRequestStatus status;
}
