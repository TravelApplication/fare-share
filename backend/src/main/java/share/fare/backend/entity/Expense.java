package share.fare.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Expense extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    private User paidByUser;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private SplitType splitType;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpenseAllocation> expenseAllocations = new ArrayList<>();

    public void addAllocation(User user, BigDecimal amountOwed, BigDecimal percentage) {
        ExpenseAllocation allocation = new ExpenseAllocation();
        allocation.setId(new ExpenseAllocationId(this.getId(), user.getId()));
        allocation.setExpense(this);
        allocation.setUser(user);
        allocation.setAmountOwed(amountOwed);
        allocation.setPercentage(percentage);
        expenseAllocations.add(allocation);
    }
}
