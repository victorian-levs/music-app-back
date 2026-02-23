package com.github.vityan55.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "track",
        indexes = {
                @Index(name = "idx_track_main_artist", columnList = "main_artist_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_artist_id", nullable = false)
    private Artist mainArtist;

    @Column(nullable = false)
    private Long durationMs;

    @Column(nullable = false)
    private LocalDate releaseDate;
}
