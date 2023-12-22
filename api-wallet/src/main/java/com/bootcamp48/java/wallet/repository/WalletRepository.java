package com.bootcamp48.java.wallet.repository;

import com.bootcamp48.java.wallet.domain.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {
    Mono<Wallet> findFirstByCustomerId(String customerId);
}
