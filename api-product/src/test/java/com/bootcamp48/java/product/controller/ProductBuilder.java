package com.bootcamp48.java.product.controller;

import com.bootcamp48.java.product.model.Product;

public class ProductBuilder {
    public static Product getDto() {
        Product dto = new Product();
        dto.setId("636dd894a3da3a2e59d90ad2");
        dto.setType(Product.TypeEnum.PASIVOS);
        dto.setDescription("cuentas bancarias");
        return dto;
    }
}
