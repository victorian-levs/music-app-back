package com.github.vityan55.musicapp.web.user;

import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.service.storage.AvatarStorageService;
import com.github.vityan55.musicapp.service.user.UserService;
import com.github.vityan55.musicapp.service.validation.FileValidationUtils;
import com.github.vityan55.musicapp.web.auth.dto.AuthResponse;
import com.github.vityan55.musicapp.web.auth.dto.LoginResult;
import com.github.vityan55.musicapp.web.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AvatarStorageService avatarStorageService;

    @GetMapping
    public ResponseEntity<UserWithTypeDto> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUser(user.getId()));
    }

    @PostMapping("/upload-url")
    public ResponseEntity<UploadAvatarUrlResponse> getUploadUrl(@AuthenticationPrincipal User user,
                                                                     CreateAvatarUploadUrlRequest request){
        String key = "avatars/" + user.getId() + "/" + UUID.randomUUID() + "." + FileValidationUtils.getExtension(request.filename());

        String url = avatarStorageService.generateUploadURL(key, user.getId(), request);

        return ResponseEntity.ok(new UploadAvatarUrlResponse(key, url));
    }

    @PostMapping("/confirm")
    public ResponseEntity<AvatarConfirmDto> getStream(@AuthenticationPrincipal User user,
                                                      ConfirmAvatarRequest request) {
        String url = userService.confirmAvatar(user.getId(), request.objectKey());

        return ResponseEntity.ok(new AvatarConfirmDto(url));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal User user) {
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        userService.deleteUser(user.getId());

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PatchMapping("/personal")
    public ResponseEntity<UserDto> updatePersonal(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody UpdatePersonalRequest request) {
        return ResponseEntity.ok(userService.updatePersonal(user.getId(), request));
    }

    @PatchMapping("/credentials")
    public ResponseEntity<AuthResponse> updatePassword(@AuthenticationPrincipal User user,
                                                       @Valid @RequestBody UpdatePasswordRequest request) {
        LoginResult result = userService.updatePassword(user.getId(), request);

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
