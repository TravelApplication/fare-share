package share.fare.backend.service.strategy;

import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmountSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> split(Expense expense, List<User> users, Map<User, BigDecimal> shares) {
        // shares map already contains the exact amounts users should pay
        return new HashMap<>(shares);
    }
}
