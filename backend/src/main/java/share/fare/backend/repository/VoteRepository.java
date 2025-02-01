package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.User;
import share.fare.backend.entity.Vote;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByActivityAndUser(Activity activity, User user);

    Optional<Vote> findByActivity(Activity activity);

    Optional<Vote> findByIdAndUser_Id(Long voteId, Long userId);
}
