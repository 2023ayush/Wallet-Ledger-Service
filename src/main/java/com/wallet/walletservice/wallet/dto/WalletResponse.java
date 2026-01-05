package com.wallet.walletservice.wallet.dto;


public class WalletResponse {
    private Long userId;
    private double balance;

    public WalletResponse(Long userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Long getUserId() { return userId; }
    public double getBalance() { return balance; }
}