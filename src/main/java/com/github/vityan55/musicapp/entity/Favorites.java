package com.github.vityan55.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "track_id"}
        ),
        indexes = {
                @Index(name = "idx_fav_user", columnList = "user_id"),
                @Index(name = "idx_fav_track", columnList = "track_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Favorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Track track;
}
