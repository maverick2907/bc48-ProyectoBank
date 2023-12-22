package com.bootcamp48.java.account.seed;

import com.bootcamp48.java.account.domain.Account;
import com.bootcamp48.java.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class AccountSeed {

    private final AccountRepository repository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        var titular = new ArrayList<String>();
        titular.add("63859d33434879511128cf355");
        var account = Account.builder()
                .id("63859d33434879511128cf355")
                .accountNumber("193-1315150-0-35")
                .cci("002-193-00131515003512")
                .productId("63859d33434879511128cf356")
                .titularId(titular)
                .status("ACTIVA")
                .availableBalance(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(account.getId())
                    .switchIfEmpty(repository.save(account)).subscribe();
        } catch (Exception ignored) { }
    }
}
