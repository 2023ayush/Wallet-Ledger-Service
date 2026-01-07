package com.wallet.walletservice.transfer.dto;


import com.wallet.walletservice.common.enums.TransactionStatus;

public class TransferResponse {
    private Long senderId;
    private Long receiverId;
    private double amount;
    private String transactionId;
    private TransactionStatus status; // new field

    public TransferResponse(Long senderId, Long receiverId, double amount, String transactionId, TransactionStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.status = status;
    }

    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public double getAmount() { return amount; }
    public String getTransactionId() { return transactionId; }
    public TransactionStatus getStatus() { return status; }
}
