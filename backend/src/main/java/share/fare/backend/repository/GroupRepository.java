package share.fare.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import share.fare.backend.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM user_groups g WHERE EXISTS " +
            "(SELECT m FROM GroupMembership m WHERE m.group.id = g.id AND m.user.id = :userId)")
    Page<Group> findGroupsByUserMembership(@Param("userId") Long userId, Pageable pageable);

}
