package com.github.vityan55.musicapp.web.track;

import com.github.vityan55.musicapp.entity.Track;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.repository.specification.TrackFilter;
import com.github.vityan55.musicapp.service.StorageService;
import com.github.vityan55.musicapp.service.TrackService;
import com.github.vityan55.musicapp.web.track.dto.PageResponse;
import com.github.vityan55.musicapp.web.track.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;
    private final StorageService storageService;

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

    @GetMapping("/{trackId}/stream")
    public ResponseEntity<TrackStreamDto> getStream(@PathVariable Long trackId) {
        String url = storageService.generateDownloadUrl(trackId);

        return ResponseEntity.ok(new TrackStreamDto(url));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TrackDto>createTrack(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody CreateTrackRequest request
    ) {
        return ResponseEntity.ok(trackService.create(request, user.getId()));
    }

    @PostMapping("/upload-url")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UploadTrackUrlResponse> getUploadUrl(@AuthenticationPrincipal User user,
                                                               CreateTrackUploadUrlRequest request) {
        String key = "tracks/" + UUID.randomUUID() + ".mp3";

        String url = storageService.generateUploadURL(key, user.getId(), request);

        return ResponseEntity.ok(new UploadTrackUrlResponse(key, url));
    }

    @PatchMapping("/{trackId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TrackDto> updateTrack(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody UpdateTrackRequest request,
                                                @PathVariable Long trackId) {
        return ResponseEntity.ok(trackService.updateTrack(user.getId(), request, trackId));
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteTrack(@AuthenticationPrincipal User user,
                                            @PathVariable Long trackId) {
        trackService.delete(user.getId(), trackId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<PageResponse<TrackDto>> constructFromPage(Page<Track> page) {
        var content = page.getContent().stream()
                .map(track -> new TrackDto(
                        track.getId(),
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