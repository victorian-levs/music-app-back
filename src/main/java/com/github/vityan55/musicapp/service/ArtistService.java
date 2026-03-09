package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.entity.Artist;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.ArtistRepository;
import com.github.vityan55.musicapp.repository.FeatRepository;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.repository.specification.ArtistFilter;
import com.github.vityan55.musicapp.repository.specification.ArtistSpecification;
import com.github.vityan55.musicapp.web.artist.dto.ArtistDto;
import com.github.vityan55.musicapp.web.artist.dto.CreateArtistRequest;
import com.github.vityan55.musicapp.web.artist.dto.UpdateArtistRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final FeatRepository featRepository;
    private final UserRepository userRepository;

    public Page<Artist> findAll(Pageable pageable) {
        log.info("Find all artists by pageable");
        return artistRepository.findAll(pageable);
    }

    public Page<Artist> filter(ArtistFilter filter, Pageable pageable) {
        log.info("Filter artists by data: {}", filter);
        return artistRepository.findAll(ArtistSpecification.withFilter(filter), pageable);
    }

    public ArtistDto findById(Long artistId) {
        log.info("Find artist with id: {}", artistId);

        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> {
            log.warn("Artist not found with id: {}", artistId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        return new ArtistDto(
                artist.getUser().getId(),
                artist.getArtistName(),
                artist.getDescription()
        );
    }

    @Transactional
    public ArtistDto create(CreateArtistRequest request) {
        log.info("Create new artist");

        User user = userRepository.findById(request.userId()).orElseThrow(() -> {
            log.warn("User not found by id: {}", request.userId());
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        Artist artist = Artist.builder()
                .user(user)
                .artistName(request.artistName())
                .description(request.description())
                .build();

        Artist newArtist = artistRepository.save(artist);

        return new ArtistDto(
                newArtist.getUser().getId(),
                newArtist.getArtistName(),
                newArtist.getDescription()
        );
    }

    @Transactional
    public ArtistDto update(
            Long userId,
            UpdateArtistRequest request,
            Long artistId
    ) {
        log.info("Update artist by artist ID: {}", artistId);

        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> {
            log.warn("Update artist failed. artist not found by id: {}", artistId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        if (!artist.getUser().getId().equals(userId)) {
            log.warn("User with id {} is not artist with Id {}", userId, artistId);
            throw new MusicAppException(
                    "User doesn't have permissions to update this artist", HttpStatus.FORBIDDEN
            );
        }

        if (request.artistName() != null && !request.artistName().isBlank()) {
            artist.setArtistName(request.artistName());
        }

        if (request.description() != null && !request.description().isBlank()) {
            artist.setDescription(request.description());
        }

        return new ArtistDto(
                artist.getUser().getId(),
                artist.getArtistName(),
                artist.getDescription()
        );
    }

    @Transactional
    public void delete(Long userId, Long artistId) {
        log.info("Delete artist with ID: {}", artistId);

        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> {
            log.warn("Delete artist failed. artist not found by id: {}", artistId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        if (!artist.getUser().getId().equals(userId)) {
            log.warn("User with id {} is not artist with id {}", userId, artistId);
            throw new MusicAppException(
                    "User doesn't have permissions to delete this artist", HttpStatus.FORBIDDEN
            );
        }

        featRepository.deleteAllByArtistId(artistId);
        artistRepository.deleteById(artistId);
    }
}
