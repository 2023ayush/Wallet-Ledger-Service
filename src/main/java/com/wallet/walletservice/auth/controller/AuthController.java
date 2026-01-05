package com.wallet.walletservice.auth.controller;

import com.wallet.walletservice.auth.dto.LoginRequest;
import com.wallet.walletservice.auth.dto.LoginResponse;
import com.wallet.walletservice.auth.dto.RegisterRequest;
import com.wallet.walletservice.auth.dto.RegisterResponse;
import com.wallet.walletservice.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // REGISTER API
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // LOGIN API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
