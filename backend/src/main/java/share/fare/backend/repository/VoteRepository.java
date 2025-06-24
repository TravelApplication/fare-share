package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.User;
import share.fare.backend.entity.Vote;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByActivityAndUser(Activity activity, User user);

    Optional<Vote> findByActivity(Activity activity);

    Optional<Vote> findByIdAndUser_Id(Long voteId, Long userId);

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.activity.group.id = :groupId AND v.user.id = :userId")
    void deleteUserVotesInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);

}
