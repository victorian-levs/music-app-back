package com.github.vityan55.musicapp.web.artist;

import com.github.vityan55.musicapp.entity.Artist;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.repository.specification.ArtistFilter;
import com.github.vityan55.musicapp.service.ArtistService;
import com.github.vityan55.musicapp.web.artist.dto.ArtistDto;
import com.github.vityan55.musicapp.web.artist.dto.UpdateArtistRequest;
import com.github.vityan55.musicapp.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<PageResponse<ArtistDto>> getAllArtists(@RequestParam Integer pageSize,
                                                                 @RequestParam Integer pageNumber) {
        return constructFromPage(artistService.findAll(PageRequest.of(pageNumber, pageSize)));
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<ArtistDto>> filterArtists(ArtistFilter filter) {
        return constructFromPage(artistService.filter(
                filter,
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ));
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDto> getArtistById(@PathVariable Long artistId) {
        return ResponseEntity.ok(artistService.findById(artistId));
    }

    @PatchMapping("/{artistId}")
    public ResponseEntity<ArtistDto> updateArtist(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody UpdateArtistRequest request,
                                                  @PathVariable Long artistId) {
        return ResponseEntity.ok(artistService.update(user.getId(), request, artistId));
    }

    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> deleteArtist(@AuthenticationPrincipal User user,
                                             @PathVariable Long artistId) {
        artistService.delete(user.getId(), artistId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<PageResponse<ArtistDto>> constructFromPage(Page<Artist> page) {
        var content = page.getContent().stream()
                .map(artist -> new ArtistDto(
                        artist.getId(),
                        artist.getUser().getId(),
                        artist.getArtistName(),
                        artist.getDescription())
                )
                .toList();
        return ResponseEntity.ok(new PageResponse<>(content, page.getTotalPages()));
    }
}