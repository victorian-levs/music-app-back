package com.github.vityan55.musicapp.web.admin;

import com.github.vityan55.musicapp.service.ArtistService;
import com.github.vityan55.musicapp.web.artist.dto.ArtistDto;
import com.github.vityan55.musicapp.web.artist.dto.CreateArtistRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ArtistService artistService;

    @PostMapping("/artists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistDto> createArtist(@Valid @RequestBody CreateArtistRequest request) {
        return ResponseEntity.ok(artistService.create(request));
    }
}