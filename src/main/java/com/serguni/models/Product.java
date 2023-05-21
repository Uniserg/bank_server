package com.serguni.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String name;
    private double rate;
    private byte[] logo;
    private String description;
    private byte period;

    @JsonIgnore
    private long count;
}
