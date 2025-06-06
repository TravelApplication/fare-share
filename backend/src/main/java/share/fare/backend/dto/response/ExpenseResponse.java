package share.fare.backend.dto.response;

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
public class ExpenseResponse {
    private Long id;
    private Long groupId;
    private Long paidByUserId;
    private String description;
    private BigDecimal totalAmount;
    private SplitType splitType;
    private Map<Long, BigDecimal> userShares;
    private LocalDateTime createdAt;
    private LocalDateTime expenseDate;
}
