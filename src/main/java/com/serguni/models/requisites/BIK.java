package com.serguni.models.requisites;

import com.serguni.exceptions.InvalidBankNumberException;
import com.serguni.exceptions.InvalidRKCPrefixException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BIK {

    @AllArgsConstructor
    @Getter
    public enum CountryCode {
        RUSSIA(4);

        private final int code;
    }

    @Getter
    public enum RegionCode {
        MOSCOW(45);

        private final int code;

        RegionCode(int code) {
            this.code = code;
        }
    }

    private CountryCode countryCode;
    private RegionCode regionCode;

    /**
     * Код подразделения ЦБ
     */
    private int centerBankBranch;
    /**
     * Внутренний номер банка в подразделении ЦБ
     */
    private int bankNumber;

    public BIK(CountryCode countryCode, RegionCode regionCode, int centerBankBranch, int bankNumber) {
        if (!isValidCenterBankBranch(centerBankBranch)) {
            throw new InvalidRKCPrefixException();
        }

        if (!isValidBankNumber(bankNumber)) {
            throw new InvalidBankNumberException();
        }

        this.countryCode = countryCode;
        this.regionCode = regionCode;
        this.centerBankBranch = centerBankBranch;
        this.bankNumber = bankNumber;
    }

    @Override
    public String toString() {
        return String.format("%02d%02d%02d%02d",
                countryCode.getCode(),
                regionCode.getCode(),
                centerBankBranch,
                bankNumber
        );
    }

    public static boolean isValidCenterBankBranch(int rkcPrefix) {
        return rkcPrefix > 0 && rkcPrefix < 80;
    }

    public static boolean isValidBankNumber(int bankNumber) {
        return bankNumber > 0 && bankNumber < 999;
    }
}