package share.fare.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity(name = "expense_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseAllocation {
    @EmbeddedId
    private ExpenseAllocationId id;

    @MapsId("expense")
    @ManyToOne(fetch = FetchType.LAZY)
    private Expense expense;

    @MapsId("user")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountOwed;

    @Column(precision = 5, scale = 2, nullable = true)
    private BigDecimal percentage;
}
