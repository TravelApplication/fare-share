package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
