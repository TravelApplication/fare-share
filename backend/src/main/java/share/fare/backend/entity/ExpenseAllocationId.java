package share.fare.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpenseAllocationId implements Serializable {
    private Long expense;
    private Long user;
}
