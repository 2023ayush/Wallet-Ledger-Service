package com.wallet.walletservice.transfer.controller;
import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.ledger.service.LedgerService;
import com.wallet.walletservice.transfer.dto.TransferRequest;
import com.wallet.walletservice.transfer.dto.TransferResponse;
import com.wallet.walletservice.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ledger")
public class TransferController {

    private final LedgerService ledgerService;
    private final WalletService walletService;

    public TransferController(LedgerService ledgerService, WalletService walletService) {
        this.ledgerService = ledgerService;
        this.walletService = walletService;
    }
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<?> transfer(
            @PathVariable Long userId,
            @RequestBody TransferRequest request,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 1️⃣ Ownership check
        if (!walletService.isOwner(userId, userDetails.getUsername())) {
            return ResponseEntity.status(403)
                    .body("Forbidden: You are not the owner of this wallet");
        }

        // 2️⃣ Amount validation
        if (request.getAmount() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Invalid amount: must be greater than 0");
        }

        // 3️⃣ Self-transfer check
        if (userId.equals(request.getReceiverId())) {
            return ResponseEntity.badRequest()
                    .body("Cannot transfer to the same wallet");
        }

        // ✅ 4️⃣ Generate transactionId (SYSTEM responsibility)
        String transactionId = "TXN-" + UUID.randomUUID();

        try {
            WalletTransaction transaction = ledgerService.transfer(
                    userId,
                    request.getReceiverId(),
                    request.getAmount(),
                    transactionId
            );

            TransferResponse response = new TransferResponse(
                    transaction.getSenderId(),
                    transaction.getReceiverId(),
                    transaction.getAmount(),
                    transaction.getTransactionId(),
                    transaction.getStatus()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    // 🔹 Transaction history
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransferResponse>> getTransactions(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!walletService.isOwner(userId, userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        List<TransferResponse> transactions = ledgerService.getTransactions(userId)
                .stream()
                .map(t -> new TransferResponse(t.getSenderId(), t.getReceiverId(), t.getAmount(), t.getTransactionId(),
                        t.getStatus() ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactions);
    }
}
