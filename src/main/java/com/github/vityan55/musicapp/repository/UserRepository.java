package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
