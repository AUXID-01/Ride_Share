package com.ayush.demo.controller;

import com.ayush.demo.model.User;
import com.ayush.demo.model.AuthRequest;
import com.ayush.demo.service.UserService;
import com.ayush.demo.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager manager;
    private final UserService service;
    private final JwtUtil jwt;

    public AuthController(AuthenticationManager m, UserService s, JwtUtil j) {
        this.manager = m; this.service = s; this.jwt = j;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // Basic validation: role must be present
        if (user.getRole() == null || (!user.getRole().equals("ROLE_DRIVER") && !user.getRole().equals("ROLE_USER"))) {
            throw new IllegalArgumentException("role must be ROLE_DRIVER or ROLE_USER");
        }
        return service.register(user.getUsername(), user.getPassword(), user.getRole());
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest req) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        UserDetails details = service.loadUserByUsername(req.getUsername());
        String role = details.getAuthorities().iterator().next().getAuthority();
        String token = jwt.generateToken(req.getUsername(), role);
        return Map.of("token", token);
    }
}
