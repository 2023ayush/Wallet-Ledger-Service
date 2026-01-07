package com.wallet.walletservice.auth.service;

import com.wallet.walletservice.auth.dto.LoginRequest;
import com.wallet.walletservice.auth.dto.LoginResponse;
import com.wallet.walletservice.auth.dto.RegisterRequest;
import com.wallet.walletservice.auth.dto.RegisterResponse;
import com.wallet.walletservice.auth.entity.User;
import com.wallet.walletservice.auth.repository.UserRepository;
import com.wallet.walletservice.wallet.entity.Wallet;
import com.wallet.walletservice.wallet.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletRepository walletRepository; // 🟢 Add WalletRepository

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       WalletRepository walletRepository) { // 🟢 Inject
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletRepository = walletRepository;
    }

    // REGISTER
    public RegisterResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // 🟢 Save user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // 🟢 Create wallet for new user
        Wallet wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setUserEmail(user.getEmail());
        walletRepository.save(wallet);

        return new RegisterResponse("User registered successfully");
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, "Login successful");
    }
}
