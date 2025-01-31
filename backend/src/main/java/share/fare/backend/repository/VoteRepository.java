package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
