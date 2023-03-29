package com.serguni.models.requisites;

import com.serguni.exceptions.InvalidBankNumberException;
import com.serguni.exceptions.InvalidRKCPrefixException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BIK {

    @AllArgsConstructor
    @Getter
    public enum CountryCode {
        RUSSIA(4);

        public static final Map<Integer,CountryCode> CODES = Map.of(4, RUSSIA);

        private final int code;
    }

    @Getter
    public enum RegionCode {
        MOSCOW(45);

        private final int code;

        public static final Map<Integer,RegionCode> CODES = Map.of(45, MOSCOW);

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
        return String.format("%02d%02d%02d%03d",
                countryCode.getCode(),
                regionCode.getCode(),
                centerBankBranch,
                bankNumber
        );
    }

    public static BIK parse(String bikStr) {
        CountryCode countryCode = CountryCode.CODES.get(Integer.parseInt(bikStr.substring(0, 2)));
        RegionCode regionCode = RegionCode.CODES.get(Integer.parseInt(bikStr.substring(2, 4)));
        int centerBankBranch = Integer.parseInt(bikStr.substring(4, 6));
        int bankNumber = Integer.parseInt(bikStr.substring(6, 9));

        return new BIK(countryCode, regionCode, centerBankBranch, bankNumber);
    }

    public static boolean isValidCenterBankBranch(int rkcPrefix) {
        return rkcPrefix > 0 && rkcPrefix < 80;
    }

    public static boolean isValidBankNumber(int bankNumber) {
        return bankNumber > 0 && bankNumber < 999;
    }
}