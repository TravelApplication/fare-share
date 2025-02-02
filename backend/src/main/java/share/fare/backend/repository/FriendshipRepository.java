package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    @Query("SELECT DISTINCT CASE " +
            "WHEN f.user1.id = :userId THEN f.user2.id " +
            "WHEN f.user2.id = :userId THEN f.user1.id " +
            "END " +
            "FROM Friendship f " +
            "WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<Long> findFriendIdsByUserId(@Param("userId") Long userId);

}

