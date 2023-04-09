package com.serguni.vars;

import com.serguni.models.requisites.BIK;

public class MyBankVars {
    public static final BIK MY_BANK_BIK = new BIK(BIK.CountryCode.RUSSIA, BIK.RegionCode.MOSCOW, 66, 666);
//    public static final CorrespondAccount MY_BANK_CORRESPOND_ACCOUNT = new CorrespondAccount(); // TODO: доделать
    public static final int BANK_ID = 1;
    public static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";
}
