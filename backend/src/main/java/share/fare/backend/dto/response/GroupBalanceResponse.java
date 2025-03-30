package share.fare.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBalanceResponse {
    private Long groupId;
    private Long userId;
    private BigDecimal balance;
}
