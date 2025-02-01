package share.fare.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Activity;


public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByGroup_Id(Long groupId, Pageable pageable);
}
