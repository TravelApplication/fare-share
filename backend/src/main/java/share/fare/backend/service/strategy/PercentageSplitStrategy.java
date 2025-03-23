package share.fare.backend.service.strategy;

import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> split(Expense expense, List<User> users, Map<User, BigDecimal> shares) {
        BigDecimal totalAmount = expense.getTotalAmount();
        Map<User, BigDecimal> result = new HashMap<>();

        for (Map.Entry<User, BigDecimal> entry : shares.entrySet()) {
            User user = entry.getKey();
            BigDecimal percentage = entry.getValue();
            BigDecimal shareAmount = totalAmount.multiply(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            result.put(user, shareAmount);

            expense.addAllocation(user, shareAmount, percentage);
        }
        return result;
    }
}