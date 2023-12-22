package com.bootcamp48.java.account.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Product {
    private String id;
    private String type;
    private String currency;
    private String description;
    private BankAccount bankAccount;
    private BankCredit bankCredit;
    private Instant createdAt;
    private Instant updatedAt;
}
