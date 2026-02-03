package com.wallet.walletservice.transfer.service;

import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.service.LedgerService;
import com.wallet.walletservice.transfer.dto.TransferRequest;
import com.wallet.walletservice.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferService {

    private final LedgerService ledgerService;
    private final WalletService walletService;

    public TransferService(LedgerService ledgerService, WalletService walletService) {
        this.ledgerService = ledgerService;
        this.walletService = walletService;
    }
     @Transactional
    public WalletTransaction transfer(Long userId, TransferRequest request, String email) {

        // 1️⃣ Ownership check
        if (!walletService.isOwner(userId, email)) {
            throw new RuntimeException("Forbidden: You are not the owner of this wallet");
        }

        // 2️⃣ Amount validation
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Invalid amount: must be greater than 0");
        }

        // 3️⃣ Self-transfer check
        if (userId.equals(request.getReceiverId())) {
            throw new RuntimeException("Cannot transfer to the same wallet");
        }

        // 4️⃣ Generate transactionId
        String transactionId = "TXN-" + UUID.randomUUID();

        // 5️⃣ Business action
        return ledgerService.transfer(
                userId,
                request.getReceiverId(),
                request.getAmount(),
                transactionId
        );
    }
}
