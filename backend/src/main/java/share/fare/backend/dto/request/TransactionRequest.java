package share.fare.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private Long debtorId; // user who owes money
    private Long creditorId; // user who is owed money
    private BigDecimal amount;
}
