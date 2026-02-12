package com.wallet.walletservice.utility;


import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class WalletHealthIndicator implements HealthIndicator {

    private final WalletRepository walletRepository;

    public WalletHealthIndicator(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Health health() {
        try {
            long count = walletRepository.count();
            return Health.up().withDetail("walletCount", count).build();
        } catch (Exception e) {
            return Health.down(e).withDetail("error", "Cannot access wallet table").build();
        }
    }
}
