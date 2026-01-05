package com.wallet.walletservice.wallet.controller;

import com.wallet.walletservice.auth.service.JwtService;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.service.WalletService;
import com.wallet.walletservice.wallet.dto.WalletRequest;
import com.wallet.walletservice.wallet.dto.WalletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final JwtService jwtService;

    public WalletController(WalletService walletService, JwtService jwtService) {
        this.walletService = walletService;
        this.jwtService = jwtService;
    }

    // 🔹 View wallet balance
    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWalletBalance(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        // Check if the logged-in user matches the requested userId
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!walletService.isOwner(userId, userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Wallet wallet = walletService.getWalletByUserId(userId);
        WalletResponse response = new WalletResponse(wallet.getUserId(), wallet.getBalance());
        return ResponseEntity.ok(response);
    }

    // 🔹 Top-up wallet
    @PostMapping("/{userId}/topup")
    public ResponseEntity<WalletResponse> topUpWallet(
            @PathVariable Long userId,
            @RequestBody WalletRequest request,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!walletService.isOwner(userId, userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Wallet updatedWallet = walletService.topUp(userId, request.getAmount());
        WalletResponse response = new WalletResponse(updatedWallet.getUserId(), updatedWallet.getBalance());
        return ResponseEntity.ok(response);

    }

    // 🔹 Withdraw from wallet
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WalletResponse> withdrawFromWallet(
            @PathVariable Long userId,
            @RequestBody WalletRequest request,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!walletService.isOwner(userId, userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Wallet updatedWallet = walletService.withdraw(userId, request.getAmount());
        WalletResponse response = new WalletResponse(updatedWallet.getUserId(), updatedWallet.getBalance());
        return ResponseEntity.ok(response);
    }
}
