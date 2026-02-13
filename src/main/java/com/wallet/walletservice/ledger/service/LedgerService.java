package com.wallet.walletservice.ledger.service;

import com.wallet.walletservice.common.enums.TransactionStatus;
import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.repository.LedgerRepository;
import com.wallet.walletservice.utility.TransactionLogger;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletTransactionRepository;
import com.wallet.walletservice.wallet.service.WalletServiceF;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LedgerService {

    private static final Logger logger = LoggerFactory.getLogger(LedgerService.class);

    private final WalletServiceF walletServiceF;
    private final LedgerRepository ledgerRepository;
    private final LedgerAuditService ledgerAuditService;
    private final WalletTransactionRepository txnRepository;
    private final MeterRegistry meterRegistry;

    public LedgerService(WalletServiceF walletServiceF,
                         LedgerRepository ledgerRepository,
                         LedgerAuditService ledgerAuditService,
                         WalletTransactionRepository txnRepository,
                         MeterRegistry meterRegistry) {
        this.walletServiceF = walletServiceF;
        this.ledgerRepository = ledgerRepository;
        this.ledgerAuditService = ledgerAuditService;
        this.txnRepository = txnRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public WalletTransaction transfer(Long senderId, Long receiverId, double amount) {
        logger.info("Initiating transfer: {} from senderId: {} to receiverId: {}", amount, senderId, receiverId);
        meterRegistry.counter("transactions.initiated").increment();

        if (amount <= 0) {
            logger.warn("Transfer failed: invalid amount {} from senderId: {}", amount, senderId);
            throw new RuntimeException("Amount not valid");
        }

        String transactionId = TransactionLogger.generateTransactionId("TRANSFER", senderId, receiverId, amount);
        logger.info("Generated transaction ID: {}", transactionId);

        // 1️⃣ Create PENDING transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        ledgerRepository.save(transaction);

        try {
            // 2️⃣ Withdraw from sender
            Wallet senderWallet = walletServiceF.withdraw(senderId, amount);
            logger.info("Withdrawal successful: {} from senderId: {}", amount, senderId);

            // 3️⃣ Top-up receiver
            Wallet receiverWallet = walletServiceF.topUp(receiverId, amount);
            logger.info("Top-up successful: {} to receiverId: {}", amount, receiverId);

            // 4️⃣ Mark SUCCESS
            transaction.setStatus(TransactionStatus.SUCCESS);
            ledgerRepository.save(transaction);
            TransactionLogger.logTransactionStatus(transactionId, "SUCCESS");
            logger.info("Transaction SUCCESS: ID {}", transactionId);

            meterRegistry.counter("transactions.success").increment();

        } catch (RuntimeException ex) {
            // 5️⃣ Save FAILED transaction using LedgerAuditService
            ledgerAuditService.saveFailedTransaction(senderId, receiverId, amount);
            TransactionLogger.logTransactionStatus(transactionId, "FAILED");
            logger.error("Transaction FAILED: ID {}. Reason: {}", transactionId, ex.getMessage());

            meterRegistry.counter("transactions.failed").increment();

            throw ex; // propagate to controller
        }

        return transaction;
    }

    public List<WalletTransaction> getTransactions(Long userId) {
        logger.info("Fetching transactions for userId: {}", userId);
        return ledgerRepository.findBySenderIdOrReceiverId(userId, userId);
    }

    public List<WalletTransaction> getTransactionsByUserId(Long userId) {
        logger.info("Fetching transaction history for userId: {}", userId);
        List<WalletTransaction> txns = txnRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);
        logger.info("Found {} transactions for userId: {}", txns.size(), userId);
        return txns;
    }
}
