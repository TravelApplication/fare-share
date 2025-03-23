package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.ExpenseAllocation;

public interface ExpenseAllocationRepository extends JpaRepository<ExpenseAllocation, Long> {
}
