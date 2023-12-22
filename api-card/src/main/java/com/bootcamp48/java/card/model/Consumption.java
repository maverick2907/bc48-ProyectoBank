package com.bootcamp48.java.card.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class Consumption {
    private String id;
    private String cardId;
    private Integer billingDay;
    private LocalDate paymentDueDate;
    private BigDecimal amount;
    private BigDecimal billedInterestsAmount; //0.5
    private Integer fixedInstallmentsQuantity; //2
    private Integer currentFixedQuota; //1
    private BigDecimal fixedPayAmount; //450
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
