package share.fare.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class FriendshipId implements Serializable {
    private Long user1Id;
    private Long user2Id;

    public FriendshipId(Long user1Id, Long user2Id) {
        if (user1Id < user2Id) {
            this.user1Id = user1Id;
            this.user2Id = user2Id;
        } else {
            this.user1Id = user2Id;
            this.user2Id = user1Id;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(user1Id, that.user1Id) &&
                Objects.equals(user2Id, that.user2Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1Id, user2Id);
    }
}
