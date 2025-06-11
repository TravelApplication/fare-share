package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.response.GroupBalanceResponse;
import share.fare.backend.dto.response.TransactionResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.repository.GroupBalanceRepository;
import share.fare.backend.repository.GroupRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupBalanceServiceTest {

    @Mock
    private GroupBalanceRepository groupBalanceRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupBalanceService groupBalanceService;

    private User testUser1;
    private User testUser2;
    private Group testGroup;
    private Expense testExpense;
    private ExpenseAllocation testAllocation1;
    private ExpenseAllocation testAllocation2;

    @BeforeEach
    public void setUp() {
        testUser1 = User.builder()
                .id(1L)
                .email("user1@test.com")
                .password("password")
                .build();

        testUser2 = User.builder()
                .id(2L)
                .email("user2@test.com")
                .password("password")
                .build();

        testGroup = Group.builder()
                .id(1L)
                .name("Test Group")
                .description("Test Description")
                .build();

        testExpense = Expense.builder()
                .id(1L)
                .description("Test Expense")
                .totalAmount(new BigDecimal("100.00"))
                .paidByUser(testUser1)
                .group(testGroup)
                .build();

        testAllocation1 = ExpenseAllocation.builder()
                .user(testUser1)
                .amountOwed(new BigDecimal("50.00"))
                .expense(testExpense)
                .build();

        testAllocation2 = ExpenseAllocation.builder()
                .user(testUser2)
                .amountOwed(new BigDecimal("50.00"))
                .expense(testExpense)
                .build();

        testExpense.setExpenseAllocations(List.of(testAllocation1, testAllocation2));
    }

    @Test
    public void testUpdateBalances_ExistingBalances() {
        GroupBalance existingPayerBalance = new GroupBalance(testGroup, testUser1, new BigDecimal("200.00"));
        GroupBalance existingDebtorBalance = new GroupBalance(testGroup, testUser2, new BigDecimal("-100.00"));

        when(groupBalanceRepository.findByGroupAndUser(testGroup, testUser1))
                .thenReturn(Optional.of(existingPayerBalance));
        when(groupBalanceRepository.findByGroupAndUser(testGroup, testUser2))
                .thenReturn(Optional.of(existingDebtorBalance));

        groupBalanceService.updateBalances(testExpense);

        assertEquals(new BigDecimal("250.00"), existingPayerBalance.getBalance());
        assertEquals(new BigDecimal("-150.00"), existingDebtorBalance.getBalance());
        verify(groupBalanceRepository, times(3)).save(any(GroupBalance.class));
    }

    @Test
    public void testGetBalances_Success() {
        GroupBalance balance1 = new GroupBalance(testGroup, testUser1, new BigDecimal("100.00"));
        GroupBalance balance2 = new GroupBalance(testGroup, testUser2, new BigDecimal("-50.00"));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupBalanceRepository.findByGroup(testGroup)).thenReturn(List.of(balance1, balance2));

        List<GroupBalanceResponse> result = groupBalanceService.getBalances(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(groupRepository, times(1)).findById(1L);
        verify(groupBalanceRepository, times(1)).findByGroup(testGroup);
    }

    @Test
    public void testGetBalances_GroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupBalanceService.getBalances(1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetMinimumTransactions_Success() {
        GroupBalance debtor1 = new GroupBalance(testGroup, testUser1, new BigDecimal("-50.00"));
        GroupBalance debtor2 = new GroupBalance(testGroup, testUser2, new BigDecimal("-30.00"));
        User creditorUser = User.builder().id(3L).build();
        GroupBalance creditor = new GroupBalance(testGroup, creditorUser, new BigDecimal("80.00"));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupBalanceRepository.findByGroup(testGroup)).thenReturn(List.of(debtor1, debtor2, creditor));

        List<TransactionResponse> result = groupBalanceService.getMinimumTransactions(1L);

        assertNotNull(result);
        assertEquals(2, result.size());

        TransactionResponse firstTransaction = result.get(0);
        assertEquals(new BigDecimal("30.00"), firstTransaction.getAmount());

        TransactionResponse secondTransaction = result.get(1);
        assertEquals(new BigDecimal("50.00"), secondTransaction.getAmount());

        verify(groupRepository, times(1)).findById(1L);
        verify(groupBalanceRepository, times(1)).findByGroup(testGroup);
    }

    @Test
    public void testGetMinimumTransactions_GroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupBalanceService.getMinimumTransactions(1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetMinimumTransactions_NoBalances() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupBalanceRepository.findByGroup(testGroup)).thenReturn(Collections.emptyList());

        List<TransactionResponse> result = groupBalanceService.getMinimumTransactions(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}