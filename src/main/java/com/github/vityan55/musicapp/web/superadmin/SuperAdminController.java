package com.github.vityan55.musicapp.web.superadmin;

import com.github.vityan55.musicapp.service.user.UserService;
import com.github.vityan55.musicapp.web.superadmin.dto.UpdateRoleRequest;
import com.github.vityan55.musicapp.web.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final UserService userService;

    @PatchMapping("/users/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserDto> updateRole(@PathVariable Long userId,
                                              @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(userService.updateRole(userId, request));
    }
}
