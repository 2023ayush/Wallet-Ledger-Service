package com.wallet.walletservice.wallet.service;

import com.wallet.walletservice.auth.entity.User;
import com.wallet.walletservice.auth.repository.UserRepository;
import com.wallet.walletservice.utility.WalletMetrics;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceF {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceF.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletMetrics walletMetrics;

    public WalletServiceF(UserRepository userRepository,
                          WalletRepository walletRepository,
                          WalletMetrics walletMetrics) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.walletMetrics = walletMetrics;
    }

    // 🔹 Get wallet by email
    public Wallet getWalletByEmail(String email) {
        logger.info("Fetching wallet for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", email);
                    return new RuntimeException("User not found");
                });

        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    logger.warn("Wallet not found for user: {}", email);
                    return new RuntimeException("Wallet not found");
                });
    }

    // 🔹 Top-up wallet
    public Wallet topUpByEmail(String email, double amount) {
        logger.info("Top-up requested: {} for user: {}", amount, email);

        try {
            Wallet wallet = getWalletByEmail(email);
            Wallet updated = topUp(wallet.getUserId(), amount);
            walletMetrics.incrementSuccess();
            logger.info("Top-up successful: {} for user: {}. New balance: {}", amount, email, updated.getBalance());
            return updated;
        } catch (Exception e) {
            walletMetrics.incrementFailed();
            logger.error("Top-up failed for user: {}. Reason: {}", email, e.getMessage());
            throw e;
        }
    }

    // 🔹 Withdraw wallet
    public Wallet withdrawByEmail(String email, double amount) {
        logger.info("Withdrawal requested: {} for user: {}", amount, email);

        try {
            Wallet wallet = getWalletByEmail(email);
            Wallet updated = withdraw(wallet.getUserId(), amount);
            walletMetrics.incrementSuccess();
            logger.info("Withdrawal successful: {} for user: {}. New balance: {}", amount, email, updated.getBalance());
            return updated;
        } catch (Exception e) {
            walletMetrics.incrementFailed();
            logger.error("Withdrawal failed for user: {}. Reason: {}", email, e.getMessage());
            throw e;
        }
    }

    // 🔹 Core top-up logic
    public Wallet topUp(Long userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.warn("Wallet not found for userId: {}", userId);
                    return new RuntimeException("Wallet not found");
                });

        wallet.setBalance(wallet.getBalance() + amount);
        Wallet updated = walletRepository.save(wallet);
        logger.debug("Wallet top-up completed for userId: {}. Balance: {}", userId, updated.getBalance());
        return updated;
    }

    // 🔹 Core withdraw logic
    public Wallet withdraw(Long userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.warn("Wallet not found for userId: {}", userId);
                    return new RuntimeException("Wallet not found");
                });

        if (wallet.getBalance() < amount) {
            logger.warn("Insufficient balance for withdrawal. UserId: {}, Requested: {}, Available: {}",
                    userId, amount, wallet.getBalance());
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        Wallet updated = walletRepository.save(wallet);
        logger.debug("Wallet withdrawal completed for userId: {}. Balance: {}", userId, updated.getBalance());
        return updated;
    }
}
