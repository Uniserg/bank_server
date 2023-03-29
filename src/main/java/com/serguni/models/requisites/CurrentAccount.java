package com.serguni.models.requisites;

import lombok.Getter;

import java.util.Map;

@Getter
public class CurrentAccount {

    /**
     * Номер счета первого порядка (3 разряда)
     */
    @Getter
    public enum FirstOrder {
        /**
        * Для физ. лиц
        */
        INDIVIDUAL(408);
        private final int code;

        public static final Map<Integer, FirstOrder> CODES = Map.of(408, INDIVIDUAL);

        FirstOrder(int code) {
            if (code < 1 || code > 999) {
                throw new RuntimeException(); // TODO: создать тип ошибки
            }
            this.code = code;
        }
    }

    /**
     * Номер счета второго порядка (2 разряда)
     */
    @Getter
    public enum SecondOrder {
        /**
         * Для физ. лиц
         */
        CARD(17);
        private final int code;
        public static final Map<Integer, SecondOrder> CODES = Map.of(17, CARD);

        SecondOrder(int code) {
            if (code < 1 || code > 99) {
                throw new RuntimeException(); // TODO: создать тип ошибки
            }
            this.code = code;
        }
    }

    /**
     * Код валюты. (3 разряда)
     */
    @Getter
    public enum UnitCode {
        RUR(810);

        private final int code;
        public static final Map<Integer, UnitCode> CODES = Map.of(810, RUR);

        UnitCode(int code) {
            if (code < 1 || code > 999) {
                throw new RuntimeException(); // TODO: создать тип ошибки
            }
            this.code = code;
        }
    }

    private final FirstOrder firstOrder;
    private final SecondOrder secondOrder;
    private final UnitCode unitCode;
    private final byte controlSum;
    private final long accountNumber;

    private final BIK bik;

    public CurrentAccount(FirstOrder firstOrder, SecondOrder secondOrder, UnitCode unitCode, long accountNumber, BIK bik) {
        this.firstOrder = firstOrder;
        this.secondOrder = secondOrder;
        this.unitCode = unitCode;
        this.accountNumber = accountNumber;
        this.bik = bik;
        this.controlSum = calculateControlSum(firstOrder, secondOrder, unitCode, accountNumber);

    }
    static byte reduceSum(long a, int i, int l) {
        byte[] w = {7, 1, 3};
        byte s = 0;

        for (int j = i; j > i - l; j--) {
            s = (byte) ((s + w[j % w.length] * (a % 10)) % 10);
            a = a / 10;
        }
        return s;
    }

    private byte calculateControlSum(FirstOrder firstOrder, SecondOrder secondOrder, UnitCode unitCode, long accountNumber) {
        byte[] w = {7, 1, 3};
        int i = 22;
        byte s = 0;

        s += reduceSum(accountNumber, i, 11);
        i -= 11;

        i -= 1; // контрольная сумма

        s += reduceSum(unitCode.getCode(), i, 3);
        i -= 3;

        s += reduceSum(secondOrder.getCode(), i, 2);
        i -= 2;

        s += reduceSum(firstOrder.getCode(), i, 3);
        i -= 3;

        s += reduceSum(bik.getBankNumber(), i, 3);

        return (byte) ((s * w[8 % 3]) % 10);
    }

    public static CurrentAccount parse(String accountNumber, BIK bik) {
        if (accountNumber.length() != 20) {
            throw new RuntimeException(); // TODO: сделать тип ошибки
        }

        CurrentAccount currentAccount = new CurrentAccount(
                FirstOrder.CODES.get(Integer.parseInt(accountNumber.substring(0, 3))),
                SecondOrder.CODES.get(Integer.parseInt(accountNumber.substring(3, 5))),
                UnitCode.CODES.get(Integer.parseInt(accountNumber.substring(5, 8))),
                Long.parseLong(accountNumber.substring(9)),
                bik
                );

        System.out.println(currentAccount);
        System.out.println(accountNumber);
        if (currentAccount.getControlSum() != Integer.parseInt(accountNumber.substring(8, 9))) {
            throw new RuntimeException(); // TODO: сделать тип ошибки
        }
        return currentAccount;
    }

    @Override
    public String toString() {
        return String.format("%03d%02d%03d%1d%011d",
                firstOrder.getCode(),
                secondOrder.getCode(),
                unitCode.getCode(),
                controlSum,
                accountNumber);
    }

    public static void main(String[] args) {
        System.out.println(
                new CurrentAccount(
                        FirstOrder.INDIVIDUAL,
                        SecondOrder.CARD,
                        UnitCode.RUR,
                        58350828,
                        new BIK(
                                BIK.CountryCode.RUSSIA,
                                BIK.RegionCode.MOSCOW,
                                52,
                                974
                        )
                )
        );
    }
}
