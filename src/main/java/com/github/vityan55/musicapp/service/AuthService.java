package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.web.auth.dto.LoginRequest;
import com.github.vityan55.musicapp.web.auth.dto.LoginResult;
import com.github.vityan55.musicapp.web.auth.dto.MeResponse;
import com.github.vityan55.musicapp.web.auth.dto.RegisterRequest;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.entity.UserRole;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request){
        log.info("User registration started. Email = {}", request.email());

        log.debug("Checking if user exists. Email = {}", request.email());
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed. User already exists. email = {}", request.email());
            throw new MusicAppException("User exists", HttpStatus.CONFLICT);
        }

        log.info("User successfully register. Email = {}", request.email());
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public LoginResult login(LoginRequest request) {
        log.info("User login attempt. Email = {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found by email: {}", request.email());
                    return new MusicAppException("User not found", HttpStatus.UNAUTHORIZED);
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed. Bad credentials. Email: {}", request.email());
            throw new MusicAppException("Bad credentials", HttpStatus.UNAUTHORIZED);
        }

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        log.info("User successfully logged in. Email: {}", request.email());
        return new LoginResult(access, refresh);
    }

    public LoginResult refresh(String refreshToken) {
        log.info("Refresh token attempt");

        if (refreshToken == null || !jwtService.validateToken(refreshToken)) {
            log.warn("Refresh failed. Invalid refresh token");
            throw new MusicAppException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractEmail(refreshToken);

        log.debug("Finding user by email to refresh: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> {
                    log.warn("Refresh failed. User not found for refresh token. Email: {}", email);
                    return new MusicAppException("User not found for refresh token", HttpStatus.UNAUTHORIZED);
                }
        );

        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        log.info("Token refresh successful. Email: {}", email);
        return new LoginResult(newAccess, newRefresh);
    }

    public MeResponse getCurrentUser(Authentication authentication) {
        log.info("Getting current user");
        User user = (User) authentication.getPrincipal();
        return new MeResponse(user.getId(), user.getEmail());
    }
}
