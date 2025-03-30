package share.fare.backend.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.entity.Expense;
import share.fare.backend.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SplitStrategyTest {
    @Mock
    private Expense expense;

    List<User> users;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        User user3 = User.builder()
                .id(3L)
                .build();

        users = List.of(user1, user2, user3);
    }

    @Test
    void testEqualSplitStrategy() {
        SplitStrategy strategy = new EqualSplitStrategy();
        Mockito.when(expense.getTotalAmount()).thenReturn(BigDecimal.valueOf(100));

        Map<User, BigDecimal> result = strategy.split(expense, users, Map.of());
        assertEquals(33.33, result.get(users.get(0)).doubleValue(), 0.01);
        assertEquals(33.33, result.get(users.get(1)).doubleValue(), 0.01);
        assertEquals(33.34, result.get(users.get(2)).doubleValue(), 0.01);
    }

    @Test
    void testPercentageSplitStrategy() {
        SplitStrategy strategy = new PercentageSplitStrategy();
        Mockito.when(expense.getTotalAmount()).thenReturn(BigDecimal.valueOf(200));

        Map<User, BigDecimal> shares = Map.of(
                users.get(0), BigDecimal.valueOf(50),
                users.get(1), BigDecimal.valueOf(30),
                users.get(2), BigDecimal.valueOf(20)
        );

        Map<User, BigDecimal> result = strategy.split(expense, users, shares);
        assertEquals(100, result.get(users.get(0)).doubleValue(), 0.01);
        assertEquals(60, result.get(users.get(1)).doubleValue(), 0.01);
        assertEquals(40, result.get(users.get(2)).doubleValue(), 0.01);
    }

    @Test
    void testAmountSplitStrategy() {
        SplitStrategy strategy = new AmountSplitStrategy();
        Map<User, BigDecimal> shares = Map.of(
                users.get(0), BigDecimal.valueOf(50),
                users.get(1), BigDecimal.valueOf(30),
                users.get(2), BigDecimal.valueOf(20)
        );

        Map<User, BigDecimal> result = strategy.split(expense, users, shares);
        assertEquals(50, result.get(users.get(0)).doubleValue(), 0.01);
        assertEquals(30, result.get(users.get(1)).doubleValue(), 0.01);
        assertEquals(20, result.get(users.get(2)).doubleValue(), 0.01);
    }

    @Test
    void testSharesSplitStrategy() {
        SplitStrategy strategy = new SharesSplitStrategy();
        Mockito.when(expense.getTotalAmount()).thenReturn(BigDecimal.valueOf(300));

        Map<User, BigDecimal> shares = Map.of(
                users.get(0), BigDecimal.valueOf(2),
                users.get(1), BigDecimal.valueOf(3),
                users.get(2), BigDecimal.valueOf(5)
        );

        Map<User, BigDecimal> result = strategy.split(expense, users, shares);

        assertEquals(60, result.get(users.get(0)).doubleValue(), 0.01);
        assertEquals(90, result.get(users.get(1)).doubleValue(), 0.01);
        assertEquals(150, result.get(users.get(2)).doubleValue(), 0.01);
    }

}