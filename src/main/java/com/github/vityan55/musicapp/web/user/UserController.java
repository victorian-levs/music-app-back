package com.github.vityan55.musicapp.web.user;

import com.github.vityan55.musicapp.service.UserService;
import com.github.vityan55.musicapp.web.auth.dto.AuthResponse;
import com.github.vityan55.musicapp.web.auth.dto.LoginResult;
import com.github.vityan55.musicapp.web.user.dto.UpdatePasswordRequest;
import com.github.vityan55.musicapp.web.user.dto.UpdatePersonalRequest;
import com.github.vityan55.musicapp.web.user.dto.UserDto;
import com.github.vityan55.musicapp.web.user.dto.UserWithTypeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserWithTypeDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUser(authentication));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(Authentication authentication) {
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        userService.deleteUser(authentication);

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PatchMapping("/personal")
    public ResponseEntity<UserDto> updatePersonal(Authentication authentication,
                                                  @Valid @RequestBody UpdatePersonalRequest request) {
        return ResponseEntity.ok(userService.updatePersonal(authentication, request));
    }

    @PatchMapping("/credentials")
    public ResponseEntity<AuthResponse> updatePassword(Authentication authentication,
                                                       @Valid @RequestBody UpdatePasswordRequest request) {
        LoginResult result = userService.updatePassword(authentication, request);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponse(result.accessToken()));
    }
}
