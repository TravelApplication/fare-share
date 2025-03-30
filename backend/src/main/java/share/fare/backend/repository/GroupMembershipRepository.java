package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupMembership;
import share.fare.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);

    boolean existsByGroupAndUser(Group group, User user);

    List<GroupMembership> findByGroup(Group group);

    boolean existsByGroupAndUser_Id(Group group, Long userId);
}
