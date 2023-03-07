package com.serguni.models.requisites;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CardNumber {

    @Getter
    public enum PaymentCode {
        MIR(2);

        private final int code;
        public static final Map<Integer, PaymentCode> CODES = Map.of(2, MIR);

        PaymentCode(int paymentCode) {
            this.code = paymentCode;
        }
    }

    /**
     * Код платежной системы (1 разряд)
     */
    private PaymentCode paymentCode;
    /**
     * Id банка-эмитента (5 разрядов)
     */
    private int bankId;
    /**
     * Id счета в банке (9 разрядов)
     */
    private long accountId;
    /**
     * Контрольная сумма (1 разряд)
     */
    private byte controlSum;

    public CardNumber(PaymentCode paymentCode, int bankId, long accountId) {
        this.paymentCode = paymentCode;
        this.bankId = bankId;
        this.accountId = accountId;
        this.controlSum = getControlSum(this.toString());
    }


    @Override
    public String toString() {
        return String.format("%1d%05d%09d%1d",
                paymentCode.getCode(),
                bankId,
                accountId,
                controlSum);
    }

    private byte getControlSum(String cardNumber) {

        int sum = Character.getNumericValue(cardNumber.charAt(cardNumber.length() - 1));
        int parity = cardNumber.length() % 2;
        for (int i = cardNumber.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(cardNumber.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }

        return (byte) ((10 - sum % 10) % 10);
    }
}
