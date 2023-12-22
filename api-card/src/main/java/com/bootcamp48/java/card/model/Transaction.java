package com.bootcamp48.java.card.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class Transaction {
    private String id;
    private String type;
    private String accountId;
    private BigDecimal amountPayable;
    private String destinationAccountId;
    private String destinationCardId;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
