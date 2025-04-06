package share.fare.backend.service.strategy;

import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SplitStrategy {
    Map<User, BigDecimal> split(Expense expense, List<User> users, Map<User, BigDecimal> shares);

    default void adjustLastUserShare(Map<User, BigDecimal> result, List<User> users, BigDecimal totalAmount) {
        BigDecimal totalShares = result.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal difference = totalAmount.subtract(totalShares);

        User lastUser = users.getLast();
        BigDecimal lastUserShare = result.getOrDefault(lastUser, BigDecimal.ZERO).add(difference);
        result.put(lastUser, lastUserShare);
    }
}
