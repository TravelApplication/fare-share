package share.fare.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequest {
    @NotNull(message = "Group ID cannot be null")
    @Positive(message = "Group ID must be a positive number")
    private Long groupId;

    @NotNull(message = "Debtor ID cannot be null")
    @Positive(message = "Debtor ID must be a positive number")
    private Long debtorId;

    @NotNull(message = "Creditor ID cannot be null")
    @Positive(message = "Creditor ID must be a positive number")
    private Long creditorId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 integer digits and 2 fraction digits")
    private BigDecimal amount;
}
