package com.wallet.walletservice.wallet.entity;

import com.wallet.walletservice.auth.entity.User;
import com.wallet.walletservice.common.enums.WalletStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Wallet {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private String userEmail; // Add this to check wallet ownership

    private double balance = 0.0; // Add this to store wallet balance

    @Enumerated(EnumType.STRING)
    private WalletStatus status = WalletStatus.ACTIVE;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public WalletStatus getStatus() { return status; }
    public void setStatus(WalletStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
