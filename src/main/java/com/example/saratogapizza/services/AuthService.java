package com.example.saratogapizza.services;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.domains.UserRole;


import com.example.saratogapizza.entities.User;

import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.repsonses.AuthResponse;
import com.example.saratogapizza.requests.RefreshTokenRequest;
import com.example.saratogapizza.requests.SigninRequest;
import com.example.saratogapizza.requests.SignupRequest;
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
import java.util.Optional;
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
}
