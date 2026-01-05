package com.wallet.walletservice.wallet.entity;

import com.wallet.walletservice.common.enums.WalletStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Wallet {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private WalletStatus status = WalletStatus.ACTIVE;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
