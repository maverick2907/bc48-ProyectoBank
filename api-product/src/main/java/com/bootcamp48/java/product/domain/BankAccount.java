package com.bootcamp48.java.product.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;

@Data
@Builder
public class BankAccount {
    @Indexed(unique = true, sparse = true)
    private String name;
    private BigDecimal maintenance;
    private BigDecimal transactionFee;
    private Integer movement;
    private Integer freeTransaction;
    private Integer fixedDay;
}
