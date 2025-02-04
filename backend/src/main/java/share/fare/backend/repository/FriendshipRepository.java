package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.User;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    @Query("SELECT u FROM user_table u " +
            "LEFT JOIN FETCH u.userInfo " +
            "WHERE u.id IN (" +
            "    SELECT f.id.user1Id FROM Friendship f WHERE f.id.user2Id = :userId " +
            "    UNION " +
            "    SELECT f.id.user2Id FROM Friendship f WHERE f.id.user1Id = :userId)")
    List<User> findFriendsByUserId(@Param("userId") Long userId);
}

