package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.web.auth.dto.LoginRequest;
import com.github.vityan55.musicapp.web.auth.dto.LoginResult;
import com.github.vityan55.musicapp.web.auth.dto.RegisterRequest;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.entity.UserRole;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request){
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new MusicAppException("User exists", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new MusicAppException("User not found", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new MusicAppException("Bad credentials", HttpStatus.UNAUTHORIZED);
        }

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        return new LoginResult(access, refresh);
    }

    public LoginResult refresh(String refreshToken) {
        if (refreshToken == null || !jwtService.validateToken(refreshToken)) {
            throw new MusicAppException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new MusicAppException("User not found for refresh token", HttpStatus.UNAUTHORIZED)
        );

        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        return new LoginResult(newAccess, newRefresh);
    }
}
