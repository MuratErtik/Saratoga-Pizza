package com.example.saratogapizza.controllers;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.requests.*;
import com.example.saratogapizza.responses.AuthResponse;
import com.example.saratogapizza.responses.ChangePasswordResponse;
import com.example.saratogapizza.responses.ResetPasswordResponse;
import com.example.saratogapizza.services.AuthService;
import com.example.saratogapizza.services.CustomerService;
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

    private final JwtUtils jwtUtils;

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
    //password CRUD section starting...
    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestHeader("Authorization") String jwt,
                                                                 @RequestBody ChangePasswordRequest request) throws AuthException {


        String token = jwt.substring(7).trim();
        Long userId = jwtUtils.getUserIdFromToken(token);

        ChangePasswordResponse response = authService.changePassword(request, userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) throws MessagingException {

        System.out.println("i was here");

        ResetPasswordResponse response = authService.resetPassword(request);

        ResponseEntity<ResetPasswordResponse> responseTo = new ResponseEntity<>(response, HttpStatus.CREATED);

        return responseTo;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> reset(@RequestBody ResetPasswordTokenRequest request) throws MessagingException {

        ResetPasswordResponse response = authService.resetPasswordToken(request);

        ResponseEntity<ResetPasswordResponse> responseTo = new ResponseEntity<>(response, HttpStatus.CREATED);

        return responseTo;    }
    //password CRUD section ending...

}
