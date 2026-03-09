package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.ArtistRepository;
import com.github.vityan55.musicapp.repository.SubscriptionRepository;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.security.JwtService;
import com.github.vityan55.musicapp.web.auth.dto.LoginResult;
import com.github.vityan55.musicapp.web.user.dto.UpdatePasswordRequest;
import com.github.vityan55.musicapp.web.user.dto.UpdatePersonalRequest;
import com.github.vityan55.musicapp.web.user.dto.UserDto;
import com.github.vityan55.musicapp.web.user.dto.UserWithTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ArtistRepository artistRepository;
    private final JwtService jwtService;

    public UserWithTypeDto getUser(Long userId) {
        log.info("Getting current user with id: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Get user failed. User not found with id: {}", userId);
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        boolean isArtist = artistRepository.findByUserId(user.getId()).isPresent();

        return new UserWithTypeDto(user.getId(), user.getEmail(), user.getUsername(), isArtist);
    }

    @Transactional
    public void deleteUser (Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Delete failed. User already deleted. Id: {}", userId);
            return new MusicAppException("User already deleted", HttpStatus.NOT_FOUND);
        });

        log.info("Delete user by id: {}", user.getId());
        int countOfDelete = subscriptionRepository.deleteAllBySubscriberId(user.getId());

        log.info("Delete subscriptions. Count: {}", countOfDelete);
        userRepository.deleteById(user.getId());
    }

    @Transactional
    public UserDto updatePersonal(Long userId, UpdatePersonalRequest request) {
        log.info("Update personal data of user with id: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Update failed. User not found with id: {}", userId);
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        log.info("Saving user with id: {}", user.getId());
        user.setUsername(request.username());

        return new UserDto(user.getId(), user.getEmail(), user.getUsername());
    }

    @Transactional
    public LoginResult updatePassword(Long userId, UpdatePasswordRequest request) {
        log.info("Update credentials of user with id: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Update failed. User not found with Id: {}", userId);
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            log.warn("Update failed. Invalid credentials");
            throw new MusicAppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            log.warn("Update failed. Password not changed");
            throw new MusicAppException("Password not changed", HttpStatus.BAD_REQUEST);
        }

        log.info("Update password of user with id: {}", user.getId());
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        return new LoginResult(access, refresh);
    }
}