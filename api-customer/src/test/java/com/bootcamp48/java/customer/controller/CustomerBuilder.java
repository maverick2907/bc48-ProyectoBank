package com.bootcamp48.java.customer.controller;

import com.bootcamp48.java.customer.model.Customer;

import java.util.Collections;
import java.util.List;

public class CustomerBuilder {
    public static List<String> getIds() {
        return Collections.singletonList("636dd894a3da3a2e59d90ad2");
    }

    public static Customer getDto() {
        Customer dto = new Customer();
        dto.setId("636dd894a3da3a2e59d90ad2");
        dto.setEmail("mvalenzdelvi@gmail.com");
        dto.setPhone("962917953");
        return dto;
    }
}