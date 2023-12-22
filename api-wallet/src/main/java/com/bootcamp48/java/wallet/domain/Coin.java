package com.bootcamp48.java.wallet.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Coin {
    private String coinType;
    private BigDecimal availableAmount;
}
