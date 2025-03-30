package share.fare.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByGroupId(Long groupId, Pageable pageable);
}
