package com.serguni.utils;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class CamelCaseObjectMapperUtil {

    static final ObjectMapper om = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();


    public static <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return om.convertValue(fromValue, toValueType);
    }


}
