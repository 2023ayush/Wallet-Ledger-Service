package com.wallet.walletservice.transfer.service;

import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.service.LedgerService;
import com.wallet.walletservice.transfer.dto.TransferRequest;
import com.wallet.walletservice.utility.WalletMetrics;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.service.WalletServiceF;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferServiceF {

    private static final Logger logger = LoggerFactory.getLogger(TransferServiceF.class);

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
        logger.info("Transfer requested: {} from user: {} to userId: {}",
                request.getAmount(), email, request.getReceiverId());

        try {
            // 1️⃣ Get sender wallet
            Wallet senderWallet = walletServiceF.getWalletByEmail(email);
            Long senderUserId = senderWallet.getUserId();

            // 2️⃣ Amount validation
            if (request.getAmount() <= 0) {
                logger.warn("Invalid transfer amount: {} by user: {}", request.getAmount(), email);
                throw new RuntimeException("Invalid amount: must be greater than 0");
            }

            // 3️⃣ Self-transfer check
            if (senderUserId.equals(request.getReceiverId())) {
                logger.warn("Self-transfer attempted by userId: {}", senderUserId);
                throw new RuntimeException("Cannot transfer to the same wallet");
            }

            // 4️⃣ Business action: LedgerService handles transaction
            WalletTransaction transaction = ledgerService.transfer(
                    senderUserId,
                    request.getReceiverId(),
                    request.getAmount()
            );

            // 5️⃣ Success metric
            walletMetrics.incrementSuccess();
            logger.info("Transfer successful: {} from userId: {} to userId: {}. TransactionId: {}",
                    request.getAmount(), senderUserId, request.getReceiverId(), transaction.getTransactionId());
            return transaction;

        } catch (Exception e) {
            // 6️⃣ Failed metric
            walletMetrics.incrementFailed();
            logger.error("Transfer failed for user: {} to userId: {}. Reason: {}",
                    email, request.getReceiverId(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public List<WalletTransaction> getTransactionHistory(String email) {
        logger.info("Fetching transaction history for user: {}", email);
        Wallet wallet = walletServiceF.getWalletByEmail(email);
        Long userId = wallet.getUserId();
        List<WalletTransaction> transactions = ledgerService.getTransactionsByUserId(userId);
        logger.info("Found {} transactions for user: {}", transactions.size(), email);
        return transactions;
    }
}
