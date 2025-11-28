package com.cintie.musicprompt_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "MusicPrompt Backend",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "message", "Health check endpoint is working"
        ));
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "endpoint", "public",
                "accessible", true,
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}