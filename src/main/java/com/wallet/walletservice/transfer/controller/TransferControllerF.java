package com.wallet.walletservice.transfer.controller;

import com.wallet.walletservice.ledger.entity.WalletTransaction;
import com.wallet.walletservice.transfer.dto.TransferRequest;
import com.wallet.walletservice.transfer.service.TransferServiceF;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferControllerF {

    private final TransferServiceF transferServiceF;

    public TransferControllerF(TransferServiceF transferServiceF) {
        this.transferServiceF = transferServiceF;
    }

    @PostMapping
    public ResponseEntity<WalletTransaction> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // JWT identity

        WalletTransaction txn = transferServiceF.transfer(request, email);

        return ResponseEntity.ok(txn);
    }

    @GetMapping("/history")
    public ResponseEntity<List<WalletTransaction>> getTransactionHistory(Authentication authentication) {
        String email = authentication.getName();
        List<WalletTransaction> history = transferServiceF.getTransactionHistory(email);
        return ResponseEntity.ok(history);
    }
}

