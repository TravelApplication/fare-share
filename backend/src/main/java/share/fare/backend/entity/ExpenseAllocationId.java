package share.fare.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseAllocationId implements Serializable {
    private Long expense;
    private Long user;
}
