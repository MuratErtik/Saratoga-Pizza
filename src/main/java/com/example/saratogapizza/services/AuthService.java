package com.example.saratogapizza.services;

import com.example.saratogapizza.configs.JwtUtils;
import com.example.saratogapizza.domains.UserRole;


import com.example.saratogapizza.entities.User;

import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.repsonses.AuthResponse;
import com.example.saratogapizza.requests.RefreshTokenRequest;
import com.example.saratogapizza.requests.SigninRequest;
import com.example.saratogapizza.requests.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail())!=null) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setUserRole(UserRole.ROLE_CUSTOMER); // örnek: "USER" ya da "ADMIN"
        userRepository.save(user);

        List<String> roles = new ArrayList<>();
        roles.add(user.getUserRole().toString());
        String accessToken = jwtUtils.generateAccessToken(user.getUserId(),user.getEmail(),roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(),user.getEmail());

        // Refresh token Redis'e kaydet
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                refreshToken,
                Duration.ofDays(7) // refresh token 7 gün geçerli
        );

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
            throw new RuntimeException("User not found");
        }

        List<String> roles = new ArrayList<>();
        roles.add(user.getUserRole().toString());


        String accessToken = jwtUtils.generateAccessToken(user.getUserId(),user.getEmail(),roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(),user.getEmail());

        // Refresh token Redis'e kaydet
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getUserId(),
                refreshToken,
                Duration.ofDays(7) // refresh token 7 gün geçerli
        );

        AuthResponse authResponse = new AuthResponse();

        authResponse.setAccessToken(accessToken);

        authResponse.setRefreshToken(refreshToken);

        authResponse.setMessage("User registered successfully");

        return authResponse;
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        Long userId = jwtUtils.getUserIdFromToken(request.getRefreshToken());

        // Redis’te token var mı kontrol et
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
