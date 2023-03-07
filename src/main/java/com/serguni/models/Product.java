package com.serguni.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String name;
    private float rate;
    private byte[] logo;
    private String description;
    private byte period;
    private long count;
}
