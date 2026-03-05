package com.github.vityan55.musicapp.web.track;

import com.github.vityan55.musicapp.entity.Track;
import com.github.vityan55.musicapp.repository.specification.TrackFilter;
import com.github.vityan55.musicapp.service.TrackService;
import com.github.vityan55.musicapp.web.track.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping
    public ResponseEntity<PageResponse<TrackDto>> getAllTracks(@RequestParam Integer pageSize,
                                                               @RequestParam Integer pageNumber) {
        return constructFromPage(trackService.findAll(PageRequest.of(pageNumber, pageSize)));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<TrackDto>> filterTracks(TrackFilter filter) {
        return constructFromPage(trackService.filter(
                filter,
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TrackDto>createTrack(Authentication authentication,
                                               @Valid @RequestBody CreateTrackRequest request
    ) {
        return ResponseEntity.ok(trackService.create(request, authentication));
    }

    @PatchMapping("/{trackId}")
    public ResponseEntity<TrackDto> updateTrack(Authentication authentication,
                                                @Valid @RequestBody UpdateTrackRequest request,
                                                @PathVariable Long trackId) {
        return ResponseEntity.ok(trackService.updateTrack(authentication, request, trackId));
    }

    private ResponseEntity<PageResponse<TrackDto>> constructFromPage(Page<Track> page) {
        var content = page.getContent().stream()
                .map(track -> new TrackDto(
                        track.getTitle(),
                        new TrackArtistDto(track.getMainArtist().getId(), track.getMainArtist().getArtistName()),
                        track.getFeats().stream()
                                .map(feat -> new TrackArtistDto(
                                        feat.getArtist().getId(),
                                        feat.getArtist().getArtistName()
                                ))
                                .toList(),
                        track.getDurationMs(),
                        track.getReleaseDate()
                ))
                .toList();
        return ResponseEntity.ok(new PageResponse<>(content, page.getTotalPages()));
    }
}