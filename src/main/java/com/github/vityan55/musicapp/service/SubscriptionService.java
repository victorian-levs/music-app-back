package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.entity.Artist;
import com.github.vityan55.musicapp.entity.Subscription;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.ArtistRepository;
import com.github.vityan55.musicapp.repository.SubscriptionRepository;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.web.artist.dto.ArtistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public List<ArtistDto> getLikedArtists(Long userId) {
        log.info("Get liked artists for user with id {}", userId);

        List<Subscription> subscriptions = subscriptionRepository.findAllBySubscriberId(userId);

        return subscriptions.stream()
                .map(subscription -> new ArtistDto(
                        subscription.getArtist().getId(),
                        subscription.getArtist().getUser().getId(),
                        subscription.getArtist().getArtistName(),
                        subscription.getArtist().getDescription()
                ))
                .toList();
    }

    @Transactional
    public void like(Long userId, Long artistId) {
        log.info("Create like from user with id {} to artist with id {}", userId, artistId);

        if (userId == null || artistId == null) {
            log.warn("Like artist failed for user with id {}. Invalid input provided", userId);
            throw new MusicAppException("Invalid input provided", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User not found by id {}", userId);
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> {
            log.warn("Artist not found by id {}", artistId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        Subscription subscription = Subscription.builder()
                .subscriber(user)
                .artist(artist)
                .build();

        try {
            subscriptionRepository.save(subscription);
        } catch (DataIntegrityViolationException e) {
            throw new MusicAppException("Like already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void deleteLike(Long userId, Long artistId) {
        log.info("Delete like for user with id {} to artist with id {}", userId, artistId);

        if (userId == null || artistId == null) {
            log.warn("Delete like failed for user with id {}. Invalid input provided", userId);
            throw new MusicAppException("Invalid input provided", HttpStatus.BAD_REQUEST);
        }

        subscriptionRepository.deleteBySubscriberIdAndArtistId(userId, artistId);
    }
}