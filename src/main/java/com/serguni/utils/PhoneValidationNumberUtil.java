package com.serguni.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneValidationNumberUtil {

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();


    public static boolean isPhoneNumberValid(String phoneNumber) throws NumberParseException {
        var phone = phoneNumberUtil.parse(phoneNumber,
                Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
        return phoneNumberUtil.isValidNumber(phone);
    }
}
