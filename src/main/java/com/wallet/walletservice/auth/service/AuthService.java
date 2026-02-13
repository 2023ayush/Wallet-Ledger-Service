package com.wallet.walletservice.auth.service;

import com.wallet.walletservice.auth.dto.LoginRequest;
import com.wallet.walletservice.auth.dto.LoginResponse;
import com.wallet.walletservice.auth.dto.RegisterRequest;
import com.wallet.walletservice.auth.dto.RegisterResponse;
import com.wallet.walletservice.auth.entity.User;
import com.wallet.walletservice.auth.repository.UserRepository;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class); // ✅ Add logger

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletRepository walletRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletRepository = walletRepository;
    }

    // REGISTER
    public RegisterResponse register(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: email {} already exists", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        // Save user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        logger.info("User {} registered successfully", user.getEmail());

        // Create wallet for new user
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setUserEmail(user.getEmail());
        walletRepository.save(wallet);
        logger.info("Wallet created for user {}", user.getEmail());

        return new RegisterResponse("User registered successfully");
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: email {} not found", request.getEmail());
                    return new RuntimeException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: incorrect password for email {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        logger.info("User {} logged in successfully", request.getEmail());

        return new LoginResponse(token, "Login successful");
    }
}
