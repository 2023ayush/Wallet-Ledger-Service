package com.wallet.walletservice.ledger.service;

import com.wallet.walletservice.common.enums.TransactionStatus;
import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.repository.LedgerRepository;
import com.wallet.walletservice.utility.TransactionLogger;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletTransactionRepository;
import com.wallet.walletservice.wallet.service.WalletServiceF;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LedgerService {

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
        // Increment total initiated transactions
        meterRegistry.counter("transactions.initiated").increment();

        if (amount <= 0) throw new RuntimeException("Amount not valid");

        // Generate a unique transaction ID and log
        String transactionId = TransactionLogger.generateTransactionId("TRANSFER", senderId, receiverId, amount);

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

            // 3️⃣ Top-up receiver
            Wallet receiverWallet = walletServiceF.topUp(receiverId, amount);

            // 4️⃣ Mark SUCCESS
            transaction.setStatus(TransactionStatus.SUCCESS);
            ledgerRepository.save(transaction);
            TransactionLogger.logTransactionStatus(transactionId, "SUCCESS");

            // Increment successful transaction metric
            meterRegistry.counter("transactions.success").increment();

        } catch (RuntimeException ex) {
            // 5️⃣ Save FAILED transaction using LedgerAuditService
            ledgerAuditService.saveFailedTransaction(senderId, receiverId, amount);
            TransactionLogger.logTransactionStatus(transactionId, "FAILED");

            // Increment failed transaction metric
            meterRegistry.counter("transactions.failed").increment();

            throw ex; // propagate to controller
        }

        return transaction;
    }

    public List<WalletTransaction> getTransactions(Long userId) {
        return ledgerRepository.findBySenderIdOrReceiverId(userId, userId);
    }

    public List<WalletTransaction> getTransactionsByUserId(Long userId) {
        return txnRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);
    }
}
