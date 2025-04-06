package share.fare.backend.service.strategy;

import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharesSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> split(Expense expense, List<User> users, Map<User, BigDecimal> shares) {
        BigDecimal totalAmount = expense.getTotalAmount();
        BigDecimal totalShares = shares.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<User, BigDecimal> result = new HashMap<>();
        for (Map.Entry<User, BigDecimal> entry : shares.entrySet()) {
            User user = entry.getKey();
            BigDecimal userShares = entry.getValue();
            BigDecimal shareAmount = totalAmount.multiply(userShares).divide(totalShares, 2, RoundingMode.HALF_UP);
            result.put(user, shareAmount);
        }
        adjustLastUserShare(result, users, totalAmount);

        return result;
    }
}
