package com.github.vityan55.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artists_feat_tracks",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"artist_id", "track_id"}
        ),
        indexes = {
                @Index(name = "idx_feat_artist", columnList = "artist_id"),
                @Index(name = "idx_feat_track", columnList = "track_id")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;
}
