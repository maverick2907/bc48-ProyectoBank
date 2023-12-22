package com.bootcamp48.java.customer.mapper;

import com.bootcamp48.java.customer.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    com.bootcamp48.java.customer.model.Customer toModel(Customer customer);
    Customer toDocument(com.bootcamp48.java.customer.model.Customer customer);
}