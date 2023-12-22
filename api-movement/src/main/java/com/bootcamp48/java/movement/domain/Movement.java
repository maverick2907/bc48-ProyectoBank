package com.bootcamp48.java.movement.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Movement {
    @Id
    private String id;
    private String idTransaction;
    private double amount;
    private String transactionType;
    private String accountId;
    private String creationDate;
    private String processDate;
}
