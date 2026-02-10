package com.wallet.walletservice.wallet.controller;

import com.wallet.walletservice.wallet.dto.WalletRequest;
import com.wallet.walletservice.wallet.dto.WalletResponse;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.service.WalletService;
import com.wallet.walletservice.wallet.service.WalletServiceF;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletControllerF {

    private final WalletServiceF walletServiceF;

    public WalletControllerF(WalletServiceF walletServiceF) {
        this.walletServiceF = walletServiceF;
    }

    // 🔹 View wallet balance
    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet(Authentication authentication) {
        String email = authentication.getName();

        Wallet wallet = walletServiceF.getWalletByEmail(email);

        return ResponseEntity.ok(
                new WalletResponse(wallet.getUserId(), wallet.getBalance())
        );
    }

    // 🔹 Top-up wallet
    @PostMapping("/me/topup")
    public ResponseEntity<WalletResponse> topUpWallet(
            @RequestBody WalletRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        Wallet updatedWallet = walletServiceF.topUpByEmail(email, request.getAmount());

        return ResponseEntity.ok(
                new WalletResponse(updatedWallet.getUserId(), updatedWallet.getBalance())
        );
    }

    // 🔹 Withdraw from wallet
    @PostMapping("/me/withdraw")
    public ResponseEntity<WalletResponse> withdrawFromWallet(
            @RequestBody WalletRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        Wallet updatedWallet = walletServiceF.withdrawByEmail(email, request.getAmount());

        return ResponseEntity.ok(
                new WalletResponse(updatedWallet.getUserId(), updatedWallet.getBalance())
        );
    }
}
