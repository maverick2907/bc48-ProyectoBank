package com.bootcamp48.java.card.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class Account {
    private String id;
    private String accountNumber;
    private String cci;
    private String productId;
    private List<String> titularId = new ArrayList<>();
    private List<String> signatoryId = null;
    private String status;
    private BigDecimal availableBalance;
    private BigDecimal retainedBalance;
    private Instant createdAt;
    private Instant updatedAt;
}
