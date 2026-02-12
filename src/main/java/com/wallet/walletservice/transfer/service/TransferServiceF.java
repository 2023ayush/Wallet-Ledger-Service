package com.wallet.walletservice.transfer.service;

import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.service.LedgerService;
import com.wallet.walletservice.transfer.dto.TransferRequest;
import com.wallet.walletservice.utility.WalletMetrics;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.service.WalletServiceF;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TransferServiceF {

    private final WalletServiceF walletServiceF;
    private final LedgerService ledgerService;
    private final WalletMetrics walletMetrics;

    public TransferServiceF(WalletServiceF walletServiceF,
                            LedgerService ledgerService,
                            WalletMetrics walletMetrics) {
        this.walletServiceF = walletServiceF;
        this.ledgerService = ledgerService;
        this.walletMetrics = walletMetrics;
    }


    @Transactional
    public WalletTransaction transfer(TransferRequest request, String email) {
        try {
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

            // 4️⃣ Business action: LedgerService handles transaction ID, logging, metrics
            WalletTransaction transaction = ledgerService.transfer(
                    senderUserId,
                    request.getReceiverId(),
                    request.getAmount()
            );

            // 5️⃣ Success metric
            walletMetrics.incrementSuccess();
            return transaction;

        } catch (Exception e) {
            // 6️⃣ Failed metric
            walletMetrics.incrementFailed();
            throw e;
        }
    }


    @Transactional
    public List<WalletTransaction> getTransactionHistory(String email) {
        Wallet wallet = walletServiceF.getWalletByEmail(email);
        Long userId = wallet.getUserId();
        return ledgerService.getTransactionsByUserId(userId);
    }



}
