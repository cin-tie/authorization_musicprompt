package com.cintie.musicprompt_backend.controllers;

import com.cintie.musicprompt_backend.dto.AuthResponse;
import com.cintie.musicprompt_backend.dto.LoginRequest;
import com.cintie.musicprompt_backend.dto.RegisterRequest;
import com.cintie.musicprompt_backend.models.User;
import com.cintie.musicprompt_backend.repositories.UserRepository;
import com.cintie.musicprompt_backend.servicies.JwtService;
import com.cintie.musicprompt_backend.servicies.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Validated RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Email already exists"));
        }
        if(userRepository.existsByUsername(request.username())){
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Username already exists"));
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role("USER")
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(new AuthResponse(null, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated LoginRequest request){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            String token = jwtService.generateToken(request.username());
            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Invalid username or password"));
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String header){
        if(header == null || !header.startsWith("Bearer ")){
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Invalid authorization header"));
        }
        String token = header.substring(7);

        Date expiration = jwtService.extractExpiration(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if(ttl > 0){
            tokenBlacklistService.blacklistToken(token, ttl);
        }
        return ResponseEntity.ok(new AuthResponse(null, "Logout successful"));
    }
}
