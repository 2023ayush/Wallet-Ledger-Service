package com.wallet.walletservice.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TransactionLogger {
    private static final Logger log = LoggerFactory.getLogger(TransactionLogger.class);

    /**
     * Generates a unique transaction ID and logs it
     */
    public static String generateTransactionId(String action, Long senderId, Long receiverId, double amount) {
        String txId = UUID.randomUUID().toString();
        log.info("Transaction [{}] | Action: {} | Sender: {} | Receiver: {} | Amount: {}",
                txId, action, senderId, receiverId, amount);
        return txId;
    }

    public static void logTransactionStatus(String transactionId, String status) {
        log.info("Transaction [{}] Status: {}", transactionId, status);
    }
}
