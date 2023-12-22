package com.bootcamp48.java.customer.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class Customer {
    @Id
    private String id;
    private String type;
    private Personal personal;
    private Business business;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true, sparse = true)
    private String phone;
    @Indexed(unique = true, sparse = true)
    private String imei;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
