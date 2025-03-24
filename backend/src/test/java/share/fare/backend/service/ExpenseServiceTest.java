package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.request.ExpenseRequest;
import share.fare.backend.entity.*;
import share.fare.backend.exception.InvalidExpenseException;
import share.fare.backend.repository.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private GroupBalanceRepository groupBalanceRepository;

    @Mock
    private GroupBalanceService groupBalanceService;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Group group;
    private User paidByUser;
    private ExpenseRequest expenseRequest;
    private Expense expense;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setId(1L);

        paidByUser = new User();
        paidByUser.setId(1L);

        expenseRequest = new ExpenseRequest();
        expenseRequest.setGroupId(1L);
        expenseRequest.setPaidByUserId(1L);
        expenseRequest.setDescription("Dinner");
        expenseRequest.setTotalAmount(BigDecimal.valueOf(100.00));
        expenseRequest.setSplitType(SplitType.EQUALLY);
        expenseRequest.setUserShares(Map.of(1L, BigDecimal.valueOf(50.00), 2L, BigDecimal.valueOf(50.00)));

        expense = new Expense();
        expense.setId(1L);
        expense.setGroup(group);
        expense.setPaidByUser(paidByUser);
        expense.setDescription("Dinner");
        expense.setTotalAmount(BigDecimal.valueOf(100.00));
        expense.setSplitType(SplitType.EQUALLY);
    }

    @Test
    void addExpenseSuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(paidByUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(groupMembershipRepository.existsByGroupAndUser_Id(group, 1L)).thenReturn(true);
        when(groupMembershipRepository.existsByGroupAndUser_Id(group, 2L)).thenReturn(true);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        expenseService.addExpense(expenseRequest);

        verify(expenseRepository, times(2)).save(any(Expense.class));
        verify(groupBalanceService, times(1)).updateBalances(any(Expense.class));
    }

    @Test
    void removeExpenseSuccess() {
        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        GroupBalance paidByBalance = new GroupBalance();
        paidByBalance.setBalance(BigDecimal.valueOf(100.00));
        when(groupBalanceRepository.findByGroupAndUser(group, paidByUser))
                .thenReturn(Optional.of(paidByBalance));

        for (ExpenseAllocation allocation : expense.getExpenseAllocations()) {
            GroupBalance userBalance = new GroupBalance();
            userBalance.setBalance(BigDecimal.valueOf(50.00));
            when(groupBalanceRepository.findByGroupAndUser(group, allocation.getUser()))
                    .thenReturn(Optional.of(userBalance));
        }

        expenseService.removeExpense(1L, authenticatedUser);

        verify(expenseRepository, times(1)).delete(expense);
        verify(groupBalanceRepository, times(1)).save(any(GroupBalance.class));
    }
}