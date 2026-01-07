package com.wallet.walletservice.ledger.service;

import com.wallet.walletservice.common.enums.TransactionStatus;
import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.repository.LedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LedgerAuditService {

    private final LedgerRepository ledgerRepository;

    public LedgerAuditService(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransaction(Long senderId, Long receiverId, double amount) {
        WalletTransaction failedTransaction = new WalletTransaction();
        failedTransaction.setTransactionId("FAILED-" + System.currentTimeMillis());
        failedTransaction.setSenderId(senderId);
        failedTransaction.setReceiverId(receiverId);
        failedTransaction.setAmount(amount);
        failedTransaction.setStatus(TransactionStatus.FAILED);
        failedTransaction.setCreatedAt(LocalDateTime.now());
        ledgerRepository.save(failedTransaction);
    }
}
