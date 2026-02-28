package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {}
