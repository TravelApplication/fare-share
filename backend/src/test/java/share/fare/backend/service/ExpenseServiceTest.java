package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import share.fare.backend.dto.request.ExpenseRequest;
import share.fare.backend.dto.response.ExpenseResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.*;
import share.fare.backend.repository.*;

import java.math.BigDecimal;
import java.util.List;
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

    @Test
    void addExpenseThrowsGroupNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> expenseService.addExpense(expenseRequest));
    }

    @Test
    void addExpenseThrowsUserNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> expenseService.addExpense(expenseRequest));
    }

    @Test
    void addExpenseThrowsInvalidExpenseExceptionForNonMember() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(paidByUser));
        lenient().when(groupMembershipRepository.existsByGroupAndUser_Id(group, 1L)).thenReturn(true);
        when(groupMembershipRepository.existsByGroupAndUser_Id(group, 2L)).thenReturn(false);

        assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(expenseRequest));
    }

    @Test
    void testRemoveExpenseThrowsExpenseNotFoundException() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.removeExpense(1L, paidByUser));
    }

    @Test
    void testRemoveExpenseThrowsUnauthorizedException() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(ActionIsNotAllowedException.class, () -> expenseService.removeExpense(1L, anotherUser));
    }

    @Test
    void testGetExpensesForGroupSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Expense> expensePage = new PageImpl<>(List.of(expense));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(expenseRepository.findByGroupId(1L, pageable)).thenReturn(expensePage);

        Page<ExpenseResponse> result = expenseService.getExpensesForGroup(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getExpensesForGroupThrowsGroupNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> expenseService.getExpensesForGroup(1L, pageable));
    }

    @Test
    void updateExpenseSuccess() {
        GroupBalance paidByBalance = new GroupBalance();
        paidByBalance.setBalance(BigDecimal.valueOf(100.00));

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(paidByUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(groupMembershipRepository.existsByGroupAndUser_Id(group, 1L)).thenReturn(true);
        when(groupMembershipRepository.existsByGroupAndUser_Id(group, 2L)).thenReturn(true);

        when(groupBalanceRepository.findByGroupAndUser(group, paidByUser))
                .thenReturn(Optional.of(paidByBalance));

        expenseService.updateExpense(1L, expenseRequest, paidByUser);

        verify(expenseRepository, times(1)).save(any(Expense.class));
        verify(groupBalanceService, times(1)).updateBalances(any(Expense.class));
    }

    @Test
    void updateExpenseThrowsExpenseNotFoundException() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateExpense(1L, expenseRequest, paidByUser));
    }

    @Test
    void updateExpenseThrowsUnauthorizedException() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(ActionIsNotAllowedException.class, () -> expenseService.updateExpense(1L, expenseRequest, anotherUser));
    }

    @Test
    void reverseGroupBalanceSuccess() {
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidByUser(paidByUser);
        expense.setTotalAmount(BigDecimal.valueOf(100));

        GroupBalance paidByBalance = new GroupBalance();
        paidByBalance.setBalance(BigDecimal.valueOf(200));

        User user = new User();
        user.setId(2L);
        ExpenseAllocation allocation = new ExpenseAllocation();
        allocation.setUser(user);
        allocation.setAmountOwed(BigDecimal.valueOf(50));
        expense.setExpenseAllocations(List.of(allocation));

        GroupBalance userBalance = new GroupBalance();
        userBalance.setBalance(BigDecimal.valueOf(0));

        when(groupBalanceRepository.findByGroupAndUser(group, paidByUser))
                .thenReturn(Optional.of(paidByBalance));
        when(groupBalanceRepository.findByGroupAndUser(group, user))
                .thenReturn(Optional.of(userBalance));

        expenseService.reverseGroupBalance(expense);

        assertEquals(BigDecimal.valueOf(100), paidByBalance.getBalance()); // 200 - 100
        assertEquals(BigDecimal.valueOf(50), userBalance.getBalance()); // 0 + 50

        verify(groupBalanceRepository, times(2)).save(any(GroupBalance.class));
    }

    @Test
    void reverseGroupBalanceThrowsGroupBalanceNotFoundExceptionForPaidByUser() {
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidByUser(paidByUser);

        when(groupBalanceRepository.findByGroupAndUser(group, paidByUser))
                .thenReturn(Optional.empty());

        assertThrows(GroupBalanceNotFoundException.class, () -> expenseService.reverseGroupBalance(expense));
    }

    @Test
    void reverseGroupBalanceThrowsGroupBalanceNotFoundExceptionForUserInAllocation() {
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidByUser(paidByUser);
        expense.setTotalAmount(BigDecimal.valueOf(100));

        GroupBalance paidByBalance = new GroupBalance();
        paidByBalance.setBalance(BigDecimal.valueOf(200));

        User user = new User();
        user.setId(2L);
        ExpenseAllocation allocation = new ExpenseAllocation();
        allocation.setUser(user);
        allocation.setAmountOwed(BigDecimal.valueOf(50));
        expense.setExpenseAllocations(List.of(allocation));

        when(groupBalanceRepository.findByGroupAndUser(group, paidByUser))
                .thenReturn(Optional.of(paidByBalance));
        when(groupBalanceRepository.findByGroupAndUser(group, user))
                .thenReturn(Optional.empty());

        assertThrows(GroupBalanceNotFoundException.class, () -> expenseService.reverseGroupBalance(expense));
    }

    @Test
    void validateExpenseRequestThrowsExceptionWhenAmountDoesNotMatch() {
        ExpenseRequest request = new ExpenseRequest();
        request.setTotalAmount(BigDecimal.valueOf(100));
        request.setSplitType(SplitType.AMOUNT);
        request.setUserShares(Map.of(1L, BigDecimal.valueOf(40), 2L, BigDecimal.valueOf(30))); // Total 70, not 100

        assertThrows(InvalidExpenseException.class, () -> expenseService.validateExpenseRequest(request));
    }

    @Test
    void validateExpenseRequestThrowsExceptionWhenPercentageDoesNotMatch100() {
        ExpenseRequest request = new ExpenseRequest();
        request.setTotalAmount(BigDecimal.valueOf(100));
        request.setSplitType(SplitType.PERCENTAGE);
        request.setUserShares(Map.of(1L, BigDecimal.valueOf(40), 2L, BigDecimal.valueOf(50))); // Total 90, not 100

        assertThrows(InvalidExpenseException.class, () -> expenseService.validateExpenseRequest(request));
    }

    @Test
    void validateExpenseRequestThrowsExceptionForInvalidSplitType() {
        ExpenseRequest request = new ExpenseRequest();
        request.setTotalAmount(BigDecimal.valueOf(100));
        request.setSplitType(null);

        assertThrows(NullPointerException.class, () -> expenseService.validateExpenseRequest(request));
    }
}