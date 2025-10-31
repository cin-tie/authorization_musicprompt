package com.cintie.musicprompt_backend.controllers;

import com.cintie.musicprompt_backend.models.User;
import com.cintie.musicprompt_backend.repositories.UserRepository;
import com.cintie.musicprompt_backend.servicies.JwtService;
import com.cintie.musicprompt_backend.servicies.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public Map<String, String> register(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return Map.of("message", "User registered successfully");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> payload){
        String username = payload.get("username");
        String password = payload.get("password");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jwtService.generateToken(username);

        return Map.of("token", token);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String header){
        String token = header.substring(7);
        tokenBlacklistService.blacklistToken(token, 3600000);
        return Map.of("message", "Logged out successfully");
    }
}
