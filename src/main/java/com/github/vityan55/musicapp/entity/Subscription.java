package com.github.vityan55.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"artist_id", "subscriber_id"}
        ),
        indexes = {
                @Index(name = "idx_sub_subscriber", columnList = "subscriber_id"),
                @Index(name = "idx_sub_artist_subscriber", columnList = "artist_id, subscriber_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;
}
