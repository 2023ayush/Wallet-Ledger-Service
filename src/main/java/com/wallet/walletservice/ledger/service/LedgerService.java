package com.wallet.walletservice.ledger.service;

import com.wallet.walletservice.common.enums.TransactionStatus;
import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.repository.LedgerRepository;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LedgerService {

    private final WalletService walletService;
    private final LedgerRepository ledgerRepository;
    private final LedgerAuditService ledgerAuditService;

    public LedgerService(WalletService walletService,
                         LedgerRepository ledgerRepository,
                         LedgerAuditService ledgerAuditService) {
        this.walletService = walletService;
        this.ledgerRepository = ledgerRepository;
        this.ledgerAuditService = ledgerAuditService;
    }

    @Transactional
    public WalletTransaction transfer(Long senderId, Long receiverId, double amount, String transactionId) {
        if (amount <= 0) throw new RuntimeException("Amount not valid");

        // 1️⃣ Create transaction as PENDING
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());

        // Save PENDING transaction
        ledgerRepository.save(transaction);

        try {
            // 2️⃣ Withdraw from sender
            Wallet senderWallet = walletService.withdraw(senderId, amount);

            // 3️⃣ Top-up receiver
            Wallet receiverWallet = walletService.topUp(receiverId, amount);

            // 4️⃣ Mark SUCCESS
            transaction.setStatus(TransactionStatus.SUCCESS);
            ledgerRepository.save(transaction);

        } catch (RuntimeException ex) {
            // 5️⃣ Save FAILED transaction using LedgerAuditService
            ledgerAuditService.saveFailedTransaction(senderId, receiverId, amount);
            throw ex; // propagate error to controller
        }

        return transaction;
    }

    public List<WalletTransaction> getTransactions(Long userId) {
        return ledgerRepository.findBySenderIdOrReceiverId(userId, userId);
    }
}
