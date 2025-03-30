package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import share.fare.backend.entity.Settlement;

import java.util.Collection;
import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByGroup_Id(Long groupId);
}
