package share.fare.backend.service.strategy;

import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SplitStrategy {
    Map<User, BigDecimal> split(Expense expense, List<User> users, Map<User, BigDecimal> shares);
}
