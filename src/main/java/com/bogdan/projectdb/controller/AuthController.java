package com.bogdan.projectdb.controller;

import com.bogdan.projectdb.dto.AuthRequest;
import com.bogdan.projectdb.dto.AuthResponse;
import com.bogdan.projectdb.model.User;
import com.bogdan.projectdb.repository.UserRepository;
import com.bogdan.projectdb.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            
            AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole("USER");
            
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(401).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
    
    @PutMapping("/admin/change-role/{username}")
    public ResponseEntity<String> updateUserRole(@PathVariable String username, 
                                                @RequestBody Map<String, String> request,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            // Verify admin token
            String token = authHeader.substring(7);
            String adminUsername = jwtService.extractUsername(token);
            User admin = userRepository.findByUsername(adminUsername).orElseThrow();
            
            if (!"ADMIN".equals(admin.getRole())) {
                return ResponseEntity.status(403).body("Access denied - Admin role required");
            }
            
            // Update user role
            String role = request.get("role");
            if (role == null || role.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Role is required");
            }
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setRole(role);
            userRepository.save(user);
            
            return ResponseEntity.ok("User role updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update user role: " + e.getMessage());
        }
    }
}