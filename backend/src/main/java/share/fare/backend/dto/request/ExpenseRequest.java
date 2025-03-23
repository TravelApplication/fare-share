package share.fare.backend.dto.request;

import lombok.*;
import share.fare.backend.entity.SplitType;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {
    private Long groupId;
    private Long paidByUserId;
    private String description;
    private BigDecimal totalAmount;
    private String currency;
    private SplitType splitType;
    private Map<Long, BigDecimal> userShares;
}
