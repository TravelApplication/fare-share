package share.fare.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {
    @EmbeddedId
    private FriendshipId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user1Id")
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user2Id")
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
