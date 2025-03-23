package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.GroupBalance;

public interface GroupBalanceRepository extends JpaRepository<GroupBalance, Long> {
}
