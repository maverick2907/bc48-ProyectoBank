package com.bootcamp48.java.product.seed;

import com.bootcamp48.java.product.domain.BankAccount;
import com.bootcamp48.java.product.domain.BankCredit;
import com.bootcamp48.java.product.domain.Product;
import com.bootcamp48.java.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class ProductSeed {

    private ProductRepository repository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        addPasivoAhorro();
        addPasivoCorriente();
        addPasivoPlazo();
        addActivoPersonal();
        addActivoEmpresarial();
    }

    private void addPasivoAhorro() {
        var bank = BankAccount.builder()
                .name("AHORRO")
                .maintenance(BigDecimal.ZERO)
                .movement(10)
                .freeTransaction(5)
                .transactionFee(new BigDecimal(3))
                .build();
        var product = Product.builder()
                .id("63859d33434879511128cf355")
                .type("PASIVOS")
                .currency("PEN")
                .description("Ahorro: libre de comisión por mantenimiento y con un límite máximo de movimientos mensuales")
                .bankAccount(bank)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(product.getId())
                    .switchIfEmpty(repository.save(product)).subscribe();
        } catch (Exception ignored) { }
    }

    private void addActivoPersonal() {
        var bank = BankCredit.builder()
                .creditCard("PERSONAL")
                .numberOfCredits(1)
                .build();
        var product = Product.builder()
                .id("63859d33434879511128cf358")
                .type("ACTIVOS")
                .currency("PEN")
                .description("Personal: solo se permite un solo crédito por persona")
                .bankCredit(bank)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(product.getId())
                    .switchIfEmpty(repository.save(product)).subscribe();
        } catch (Exception ignored) { }
    }

    private void addPasivoPlazo() {
        var bank = BankAccount.builder()
                .name("PLAZO_FIJO")
                .maintenance(BigDecimal.ZERO)
                .movement(1)
                .freeTransaction(1)
                .transactionFee(BigDecimal.ZERO)
                .fixedDay(1)
                .build();
        var product = Product.builder()
                .id("63859d33434879511128cf357")
                .type("PASIVOS")
                .currency("PEN")
                .description("Plazo fijo: libre de comisión por mantenimiento, solo permite un movimiento de retiro o depósito en un día específico del mes")
                .bankAccount(bank)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(product.getId())
                    .switchIfEmpty(repository.save(product)).subscribe();
        } catch (Exception ignored) { }
    }

    private void addPasivoCorriente() {
        var bank = BankAccount.builder()
                .name("CORRIENTE")
                .maintenance(new BigDecimal(8))
                .freeTransaction(100)
                .transactionFee(new BigDecimal(5))
                .build();
        var product = Product.builder()
                .id("63859d33434879511128cf356")
                .type("PASIVOS")
                .currency("PEN")
                .description("Cuenta corriente: posee comisión de mantenimiento y sin límite de movimientos mensuales")
                .bankAccount(bank)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(product.getId())
                    .switchIfEmpty(repository.save(product)).subscribe();
        } catch (Exception ignored) { }
    }

    private void addActivoEmpresarial() {
        var bank = BankCredit.builder()
                .creditCard("EMPRESARIAL")
                .numberOfCredits(100)
                .build();
        var product = Product.builder()
                .id("63859d33434879511128cf359")
                .type("ACTIVOS")
                .currency("PEN")
                .description("Empresarial: se permite más de un crédito por empresa")
                .bankCredit(bank)
                .createdAt(Instant.now())
                .build();
        try {
            repository.findById(product.getId())
                    .switchIfEmpty(repository.save(product)).subscribe();
        } catch (Exception ignored) { }
    }
}
