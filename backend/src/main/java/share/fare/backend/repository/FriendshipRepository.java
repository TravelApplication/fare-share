package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
}

