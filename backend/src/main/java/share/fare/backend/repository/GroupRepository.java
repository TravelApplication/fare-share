package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
