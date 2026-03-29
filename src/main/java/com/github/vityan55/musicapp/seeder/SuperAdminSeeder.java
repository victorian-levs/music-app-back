package com.github.vityan55.musicapp.seeder;

import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.entity.UserRole;
import com.github.vityan55.musicapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("prod")
public class SuperAdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createSuperAdmin() {
        if (userRepository.existsByUserRole(UserRole.SUPER_ADMIN)) {
            return;
        }

        User admin = User.builder()
                .email("fazyatdenov2004@gmail.com")
                .password(passwordEncoder.encode("Qwerty1234"))
                .username("Super Admin")
                .userRole(UserRole.SUPER_ADMIN)
                .build();

        userRepository.save(admin);
    }
}