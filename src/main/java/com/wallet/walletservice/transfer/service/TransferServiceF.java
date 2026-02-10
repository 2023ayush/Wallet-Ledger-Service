package com.wallet.walletservice.transfer.service;

import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.service.LedgerService;
import com.wallet.walletservice.transfer.dto.TransferRequest;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.service.WalletServiceF;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class TransferServiceF {

    private final WalletServiceF walletServiceF;
    private final LedgerService ledgerService;

    public TransferServiceF(WalletServiceF walletServiceF, LedgerService ledgerService) {
        this.walletServiceF = walletServiceF;
        this.ledgerService = ledgerService;
    }

    @Transactional
    public WalletTransaction transfer(TransferRequest request, String email) {

        // 1️⃣ Get sender wallet from JWT
        Wallet senderWallet = walletServiceF.getWalletByEmail(email);
        Long senderUserId = senderWallet.getUserId();

        // 2️⃣ Amount validation
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Invalid amount: must be greater than 0");
        }

        // 3️⃣ Self-transfer check
        if (senderUserId.equals(request.getReceiverId())) {
            throw new RuntimeException("Cannot transfer to the same wallet");
        }

        // 4️⃣ Generate transactionId
        String transactionId = "TXN-" + UUID.randomUUID();

        // 5️⃣ Business action
        return ledgerService.transfer(
                senderUserId,
                request.getReceiverId(),
                request.getAmount(),
                transactionId
        );
    }

    @Transactional
    public List<WalletTransaction> getTransactionHistory(String email) {
        Wallet wallet = walletServiceF.getWalletByEmail(email);
        Long userId = wallet.getUserId();
        return ledgerService.getTransactionsByUserId(userId);
    }



}
