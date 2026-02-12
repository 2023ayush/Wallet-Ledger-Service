package com.wallet.walletservice.utility;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class WalletMetrics {

    private final Counter successCounter;
    private final Counter failedCounter;

    public WalletMetrics(MeterRegistry registry) {
        this.successCounter = Counter.builder("wallet_transactions_total")
                .tag("status", "SUCCESS")
                .description("Total number of successful wallet transactions")
                .register(registry);

        this.failedCounter = Counter.builder("wallet_transactions_total")
                .tag("status", "FAILED")
                .description("Total number of failed wallet transactions")
                .register(registry);
    }

    public void incrementSuccess() {
        successCounter.increment();
    }

    public void incrementFailed() {
        failedCounter.increment();
    }
}
