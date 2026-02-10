package com.wallet.walletservice.wallet.service;

import com.wallet.walletservice.auth.entity.User;
import com.wallet.walletservice.auth.repository.UserRepository;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceF {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletServiceF(UserRepository userRepository,
                         WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    // 🔹 JWT-based access
    public Wallet getWalletByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public Wallet topUpByEmail(String email, double amount) {
        Wallet wallet = getWalletByEmail(email);
        return topUp(wallet.getUserId(), amount);
    }

    public Wallet withdrawByEmail(String email, double amount) {
        Wallet wallet = getWalletByEmail(email);
        return withdraw(wallet.getUserId(), amount);
    }

    // 🔹 Existing methods (already in your service)
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
