package com.wallet.walletservice.wallet.service;

import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user id: " + userId));
    }

    public boolean isOwner(Long userId, String email) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.getUserEmail().equals(email);
    }

    public Wallet topUp(Long userId, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Top-up amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }


    public Wallet withdraw(Long userId, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Withdraw amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        return walletRepository.save(wallet);
    }



}
