package com.github.vityan55.musicapp.web.likes;

import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.service.favorites.FavoritesService;
import com.github.vityan55.musicapp.service.subscription.SubscriptionService;
import com.github.vityan55.musicapp.web.artist.dto.ArtistDto;
import com.github.vityan55.musicapp.web.track.dto.FavoriteTracksDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("likes")
@RequiredArgsConstructor
public class LikesController {

    private final SubscriptionService subscriptionService;
    private final FavoritesService favoritesService;

    @GetMapping("/artists")
    public ResponseEntity<List<ArtistDto>> getLikedArtists(@AuthenticationPrincipal User user) {
        List<ArtistDto> artists = subscriptionService.getLikedArtists(user.getId());
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/tracks")
    public ResponseEntity<FavoriteTracksDto> getLikedTracks(@AuthenticationPrincipal User user) {
        FavoriteTracksDto favoriteTracks = favoritesService.getLikedTracks(user.getId());
        return ResponseEntity.ok(favoriteTracks);
    }

    @PostMapping("artists/{artistId}")
    public ResponseEntity<Void> likeArtist(@AuthenticationPrincipal User user,
                                           @PathVariable Long artistId) {
        subscriptionService.like(user.getId(), artistId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("tracks/{trackId}")
    public ResponseEntity<Void> likeTrack(@AuthenticationPrincipal User user,
                                          @PathVariable Long trackId) {
        favoritesService.like(user.getId(), trackId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("artists/{artistId}")
    public ResponseEntity<Void> deleteLikeArtist(@AuthenticationPrincipal User user,
                                                 @PathVariable Long artistId) {
        subscriptionService.deleteLike(user.getId(), artistId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("tracks/{trackId}")
    public ResponseEntity<Void> deleteLikeTrack(@AuthenticationPrincipal User user,
                                                @PathVariable Long trackId) {
        favoritesService.deleteLike(user.getId(), trackId);
        return ResponseEntity.noContent().build();
    }
}
