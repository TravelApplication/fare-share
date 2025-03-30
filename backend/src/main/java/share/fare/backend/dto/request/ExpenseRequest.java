package share.fare.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import share.fare.backend.entity.SplitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {
    @NotNull(message = "Group ID cannot be null")
    @Positive(message = "Group ID must be a positive number")
    private Long groupId;

    @NotNull(message = "Paid by user ID cannot be null")
    @Positive(message = "Paid by user ID must be a positive number")
    private Long paidByUserId;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.01", message = "Total amount must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have up to 10 integer digits and 2 fraction digits")
    private BigDecimal totalAmount;

    @NotNull(message = "Split type cannot be null")
    private SplitType splitType;

    @NotNull(message = "User shares cannot be null")
    @Valid
    private Map<@Positive Long, @Valid BigDecimal> userShares;

    private LocalDateTime expenseDate;
}
