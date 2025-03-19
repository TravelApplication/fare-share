package share.fare.backend.service.strategy;

import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> split(Expense expense, List<User> users, Map<User, BigDecimal> shares) {
        BigDecimal totalAmount = expense.getTotalAmount();
        BigDecimal shareAmount = totalAmount.divide(BigDecimal.valueOf(users.size()), 2, RoundingMode.HALF_UP);

        Map<User, BigDecimal> result = new HashMap<>();
        for (User user : users) {
            result.put(user, shareAmount);
        }
        return result;
    }
}
