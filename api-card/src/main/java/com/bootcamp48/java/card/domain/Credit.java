package com.bootcamp48.java.card.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Credit {
    private String status;
    private BigDecimal creditLineAmount; //1000
    private BigDecimal creditLineAvailableAmount; //400
    private BigDecimal usedCreditLineAmount; // 600
    private BigDecimal accruedPaymentAmount; //0
    private BigDecimal billedMembershipFeeAmount; //100
}
