package com.bootcamp48.java.product.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class Product {
    @Id
    private String id;
    private String type;
    private String currency;
    private String description;
    private BankAccount bankAccount;
    private BankCredit bankCredit;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
