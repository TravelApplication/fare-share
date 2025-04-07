package share.fare.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {
    private Long id;
    private Long groupId;
    private Long paidByUserId;
    private Long paidToUserId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}
