package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByUserRole(UserRole userRole);
}
