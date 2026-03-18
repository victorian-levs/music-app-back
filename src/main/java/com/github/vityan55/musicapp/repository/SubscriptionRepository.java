package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Subscription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @EntityGraph(attributePaths = {"artist", "artist.user"})
    List<Subscription> findAllBySubscriberId(Long subscriberId);

    void deleteBySubscriberIdAndArtistId(Long subscriberId, Long artistId);
}