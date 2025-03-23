package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.ExpenseRequest;
import share.fare.backend.dto.response.ExpenseResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.ExpenseNotFoundException;
import share.fare.backend.exception.GroupBalanceNotFoundException;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.ExpenseMapper;
import share.fare.backend.repository.*;
import share.fare.backend.service.strategy.SplitStrategy;
import share.fare.backend.service.strategy.SplitStrategyFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseAllocationRepository expenseAllocationRepository;
    private final GroupBalanceRepository groupBalanceRepository;
    private final GroupBalanceService groupBalanceService;

    @Transactional
    public void addExpense(ExpenseRequest expenseRequest) {
        Group group = groupRepository.findById(expenseRequest.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException(expenseRequest.getGroupId()));
        User paidByUser = userRepository.findById(expenseRequest.getPaidByUserId())
                .orElseThrow(() -> new UserNotFoundException(expenseRequest.getPaidByUserId()));

        Expense expense = ExpenseMapper.toEntity(expenseRequest, group, paidByUser);
        expenseRepository.save(expense);

        SplitStrategy splitStrategy = SplitStrategyFactory.getStrategy(expenseRequest.getSplitType());

        Map<User, BigDecimal> userShares = new HashMap<>();
        for (Map.Entry<Long, BigDecimal> entry : expenseRequest.getUserShares().entrySet()) {
            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new UserNotFoundException(entry.getKey()));
            userShares.put(user, entry.getValue());
        }

        List<User> users = new ArrayList<>(userShares.keySet());
        Map<User, BigDecimal> allocations = splitStrategy.split(expense, users, userShares);

        for (Map.Entry<User, BigDecimal> entry : allocations.entrySet()) {
            User user = entry.getKey();
            BigDecimal amountOwed = entry.getValue();

            BigDecimal percentage = BigDecimal.valueOf(0);
            if (expenseRequest.getSplitType() == SplitType.PERCENTAGE) {
                percentage = expenseRequest.getUserShares().get(user.getId());
            }

            expense.addAllocation(user, amountOwed, percentage);
        }

        groupBalanceService.updateBalances(expense);

        expenseRepository.save(expense);
    }

    @Transactional
    public void removeExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        reverseGroupBalance(expense);

        expenseRepository.delete(expense);
    }

    public Page<ExpenseResponse> getExpensesForGroup(Long groupId, Pageable pageable) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        return expenseRepository.findByGroupId(groupId, pageable)
                .map(ExpenseMapper::toResponse);
    }

    private void reverseGroupBalance(Expense expense) {
        Group group = expense.getGroup();
        User paidByUser = expense.getPaidByUser();
        BigDecimal totalAmount = expense.getTotalAmount();

        GroupBalance paidByBalance = groupBalanceRepository.findByGroupAndUser(group, paidByUser)
                .orElseThrow(() -> new GroupBalanceNotFoundException(group.getId(), paidByUser.getId()));
        paidByBalance.setBalance(paidByBalance.getBalance().subtract(totalAmount));
        groupBalanceRepository.save(paidByBalance);

        for (ExpenseAllocation allocation : expense.getExpenseAllocations()) {
            User user = allocation.getUser();
            BigDecimal amountOwed = allocation.getAmountOwed();

            GroupBalance userBalance = groupBalanceRepository.findByGroupAndUser(group, user)
                    .orElseThrow(() -> new GroupBalanceNotFoundException(group.getId(), user.getId()));
            userBalance.setBalance(userBalance.getBalance().add(amountOwed));
            groupBalanceRepository.save(userBalance);
        }
    }

}
