package com.bootcamp48.java.transaction.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class Transaction {
    @Id
    private String id;
    private String transactionType;
    private double amount;
    private String accountId;
    @CreatedDate
    private Instant createdAt;

}
