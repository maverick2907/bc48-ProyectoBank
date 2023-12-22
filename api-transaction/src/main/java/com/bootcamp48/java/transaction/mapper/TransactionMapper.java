package com.bootcamp48.java.transaction.mapper;

import com.bootcamp48.java.transaction.domain.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    com.bootcamp48.java.transaction.model.Transaction toModel(Transaction transaction);
    Transaction toDocument(com.bootcamp48.java.transaction.model.Transaction transaction);
}
