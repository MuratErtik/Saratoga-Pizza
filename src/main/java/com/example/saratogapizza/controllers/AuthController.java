package com.example.saratogapizza.controllers;

import com.example.saratogapizza.responses.AuthResponse;
import com.example.saratogapizza.requests.RefreshTokenRequest;
import com.example.saratogapizza.requests.SigninRequest;
import com.example.saratogapizza.requests.SignupRequest;
import com.example.saratogapizza.services.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request,@RequestParam String role) throws MessagingException {
        AuthResponse authResponse = authService.signup(request,role);
        ResponseEntity<AuthResponse> response = new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        return response;
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logged out successfully");
    }
}
