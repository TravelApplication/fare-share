package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupBalance;
import share.fare.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface GroupBalanceRepository extends JpaRepository<GroupBalance, Long> {
    Optional<GroupBalance> findByGroupAndUser(Group group, User paidByUser);

    List<GroupBalance> findByGroup(Group group);
}
