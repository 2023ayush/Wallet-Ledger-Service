package com.wallet.walletservice.wallet.service;

import com.wallet.walletservice.auth.entity.User;
import com.wallet.walletservice.auth.repository.UserRepository;
import com.wallet.walletservice.utility.WalletMetrics;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
@Service
public class WalletServiceF {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletMetrics walletMetrics;  // ← Inject metrics

    public WalletServiceF(UserRepository userRepository,
                          WalletRepository walletRepository,
                          WalletMetrics walletMetrics) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.walletMetrics = walletMetrics;  // ← Initialize
    }

    // 🔹 JWT-based access
    public Wallet getWalletByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public Wallet topUpByEmail(String email, double amount) {
        try {
            Wallet wallet = getWalletByEmail(email);
            Wallet updated = topUp(wallet.getUserId(), amount);
            walletMetrics.incrementSuccess();  // ← Success
            return updated;
        } catch (Exception e) {
            walletMetrics.incrementFailed();  // ← Failed
            throw e;
        }
    }

    public Wallet withdrawByEmail(String email, double amount) {
        try {
            Wallet wallet = getWalletByEmail(email);
            Wallet updated = withdraw(wallet.getUserId(), amount);
            walletMetrics.incrementSuccess();  // ← Success
            return updated;
        } catch (Exception e) {
            walletMetrics.incrementFailed();  // ← Failed
            throw e;
        }
    }

    public Wallet topUp(Long userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Long userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        return walletRepository.save(wallet);
    }
}
