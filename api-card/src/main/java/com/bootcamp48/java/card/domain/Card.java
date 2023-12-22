package com.bootcamp48.java.card.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Document
public class Card {
    @Id
    private String id;
    @Indexed(unique = true)
    private String numberCard;
    private String type;
    private String status;
    private LocalDate expiration;
    private String cvv;
    private BigDecimal cashWithdrawalAllowed;
    private String productId;
    private String titularId;
    private String primaryAccountId;
    private List<String> associatedAccountId = null;
    private Credit credit;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
