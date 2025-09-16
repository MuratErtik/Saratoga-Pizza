package com.example.saratogapizza.services;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.domains.UserRole;


import com.example.saratogapizza.entities.User;

import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.requests.*;
import com.example.saratogapizza.responses.AuthResponse;
import com.example.saratogapizza.responses.ChangePasswordResponse;
import com.example.saratogapizza.responses.ResetPasswordResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final StringRedisTemplate redisTemplate;

    private final EmailService emailService;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public AuthResponse signup(SignupRequest request,String role) throws AuthException, MessagingException {

        if (userRepository.findByEmail(request.getEmail())!=null) {
            throw new AuthException("Email already in use");
        }

        User user = new User();

        user.setEmail(request.getEmail());

        user.setName(request.getName());

        user.setLastname(request.getLastname());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (role.equals("admin")) {
            user.setUserRole(UserRole.ROLE_ADMIN);
        }
        else{
            user.setUserRole(UserRole.ROLE_CUSTOMER);
        }

        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);

//        emailService.afterTheRegister(user.getEmail());

        List<String> roles = new ArrayList<>();

        roles.add(user.getUserRole().toString());

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(),user.getEmail(),roles);

        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(),user.getEmail());

        //  save to redis the refresh token
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                refreshToken,
                Duration.ofDays(7) // refresh token valid for 7day
        );

        // Generate Verification code and save to Redis
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        String key = "email:verify:" + user.getUserId();
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        // send Mail
        emailService.sendVerificationEmail(user.getEmail(), code,user.getName(),user.getLastname());

        AuthResponse authResponse = new AuthResponse();

        authResponse.setAccessToken(accessToken);

        authResponse.setRefreshToken(refreshToken);

        authResponse.setMessage("User registered successfully");

        return authResponse;

    }

    public AuthResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new AuthException("User not found");
        }

        List<String> roles = new ArrayList<>();

        roles.add(user.getUserRole().toString());

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(),user.getEmail(),roles);

        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(),user.getEmail());

        // Refresh token save to redis
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                refreshToken,
                Duration.ofDays(7) // refresh token valid for 7 days
        );

        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();

        authResponse.setAccessToken(accessToken);

        authResponse.setRefreshToken(refreshToken);

        authResponse.setMessage("User registered successfully");

        return authResponse;
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        Long userId = jwtUtils.getUserIdFromToken(request.getRefreshToken());

        // check is there a token in Redis
        String storedRefresh = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        if (storedRefresh == null || !storedRefresh.equals(request.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = new ArrayList<>();

        roles.add(user.getUserRole().toString());



        String newAccessToken = jwtUtils.generateAccessToken(user.getUserId(),user.getEmail(),roles );
        AuthResponse authResponse = new AuthResponse();

        authResponse.setAccessToken(newAccessToken);

        authResponse.setRefreshToken(storedRefresh);

        authResponse.setMessage("Token refreshed successfully");

        return authResponse;
    }

    public void logout(RefreshTokenRequest request) {
        Long userId = jwtUtils.getUserIdFromToken(request.getRefreshToken());
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }


    //change password section staring...
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, Long userId) {

        User user = userRepository.findByUserId(userId);


        if (user == null) {
            throw new AuthException("User not found");
        }

        if (!user.getEmail().equals(request.getMail())) {
            throw new AuthException("The user does not find with email");

        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthException("The passwords does not match");
        }

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AuthException("Old password has been incorrect!");

        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse();

        changePasswordResponse.setMessage("Password changed successfully");

        return changePasswordResponse;

    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) throws MessagingException {


        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new AuthException("If this email is registered, a reset link has been sent.");
        }

        String token = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("password:reset:" + user.getUserId(), token, 15, TimeUnit.MINUTES);

        String url = "http://localhost:8080/resetPassword?userId=" + user.getUserId() + "&token=" + token;

        emailService.resetPasswordMail(user,url);

        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();

        resetPasswordResponse.setMessage("If this email is registered, a reset link has been sent.");

        return resetPasswordResponse;


    }

    public ResetPasswordResponse resetPasswordToken(ResetPasswordTokenRequest request) {

        String savedToken = redisTemplate.opsForValue().get("password:reset:" + request.getUserId());

        if (savedToken == null || !savedToken.equals(request.getToken())) {
            throw new  AuthException("Invalid or expired token");
        }

        User user = userRepository.findByUserId(request.getUserId());
        if (user == null) throw new  AuthException("User not found");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete("password:reset:" + request.getUserId());

        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        resetPasswordResponse.setMessage("Password changed successfully");
        return resetPasswordResponse;
    }
    //change password section ending...
}
