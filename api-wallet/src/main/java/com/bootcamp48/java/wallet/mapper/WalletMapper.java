package com.bootcamp48.java.wallet.mapper;

import com.bootcamp48.java.wallet.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toModel(com.bootcamp48.java.wallet.domain.Wallet wallet);
    com.bootcamp48.java.wallet.domain.Wallet toDocument(Wallet wallet);
}
