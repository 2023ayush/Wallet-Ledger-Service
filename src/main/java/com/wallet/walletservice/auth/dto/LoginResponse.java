package com.wallet.walletservice.auth.dto;

public class LoginResponse {
    private String token;
    private String message;
    public LoginResponse(String token, String message) { this.token = token; this.message = message; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}