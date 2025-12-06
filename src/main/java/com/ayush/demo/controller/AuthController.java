package com.ayush.demo.controller;

import jakarta.validation.Valid;
import com.ayush.demo.dto.LoginRequest;
import com.ayush.demo.dto.RegisterRequest;
import com.ayush.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String result = authService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }
    @GetMapping("/test")
    public ResponseEntity<?> testAuth() {
        // This will return the username of the currently logged-in user
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        String role = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_UNKNOWN");

        return ResponseEntity.ok("Hello " + username + ", your role is " + role);
    }

}