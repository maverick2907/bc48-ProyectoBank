package com.bootcamp48.java.account.mapper;

import com.bootcamp48.java.account.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toModel(com.bootcamp48.java.account.domain.Account account);
    com.bootcamp48.java.account.domain.Account toDocument(Account product);
}