package com.wallet.walletservice.auth.service;

import com.wallet.walletservice.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // Use Keys.hmacShaKeyFor to ensure the key is valid for HS256
    private static final String SECRET_STRING = "thisisaverysecuresecretkeythatiswaytoolong123!";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes()); // 32+ bytes
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setId(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)  // parserBuilder for newer versions
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
