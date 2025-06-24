package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.ExpenseRequest;
import share.fare.backend.dto.response.ExpenseResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.*;
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
    private final GroupBalanceRepository groupBalanceRepository;
    private final GroupBalanceService groupBalanceService;
    private final GroupMembershipRepository groupMembershipRepository;

    @Transactional
    public void addExpense(ExpenseRequest expenseRequest) {
        validateExpenseRequest(expenseRequest);

        Group group = getGroupById(expenseRequest.getGroupId());
        User paidByUser = getUserById(expenseRequest.getPaidByUserId());

        validateUserSplitsBelongToGroup(group, expenseRequest.getUserShares());

        Expense expense = createExpense(expenseRequest, group, paidByUser);

        resolveSplitStrategy(expense, expenseRequest);

        groupBalanceService.updateBalances(expense);
        expenseRepository.save(expense);
    }

    @Transactional
    public void removeExpense(Long expenseId, User authenticatedUser) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        if (!expense.getPaidByUser().getId().equals(authenticatedUser.getId())) {
            throw new ActionIsNotAllowedException("You are not authorized to delete this expense.");
        }

        reverseGroupBalance(expense);
        expenseRepository.delete(expense);
    }

    public Page<ExpenseResponse> getExpensesForGroup(Long groupId, Pageable pageable) {
        getGroupById(groupId);
        return expenseRepository.findByGroupId(groupId, pageable)
                .map(ExpenseMapper::toResponse);
    }

    @Transactional
    public void updateExpense(Long expenseId, ExpenseRequest expenseRequest, User authenticatedUser) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        if (!existingExpense.getPaidByUser().getId().equals(authenticatedUser.getId())) {
            throw new ActionIsNotAllowedException("You are not authorized to update this expense.");
        }

        Group group = getGroupById(expenseRequest.getGroupId());

        validateUserSplitsBelongToGroup(group, expenseRequest.getUserShares());

        reverseGroupBalance(existingExpense);
        updateExpenseFields(existingExpense, expenseRequest);
        expenseRepository.save(existingExpense);

        groupBalanceService.updateBalances(existingExpense);
    }

    private Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Expense createExpense(ExpenseRequest expenseRequest, Group group, User paidByUser) {
        Expense expense = ExpenseMapper.toEntity(expenseRequest, group, paidByUser);
        expenseRepository.save(expense);
        return expense;
    }

    private void updateExpenseFields(Expense expense, ExpenseRequest expenseRequest) {
        expense.setDescription(expenseRequest.getDescription());
        expense.setTotalAmount(expenseRequest.getTotalAmount());
        expense.setSplitType(expenseRequest.getSplitType());

        expense.getExpenseAllocations().clear();

        Group group = getGroupById(expenseRequest.getGroupId());
        User paidByUser = getUserById(expenseRequest.getPaidByUserId());
        expense.setGroup(group);
        expense.setPaidByUser(paidByUser);

        resolveSplitStrategy(expense, expenseRequest);
    }

    private void resolveSplitStrategy(Expense expense, ExpenseRequest expenseRequest) {
        SplitStrategy splitStrategy = SplitStrategyFactory.getStrategy(expenseRequest.getSplitType());
        Map<User, BigDecimal> userShares = getUserShares(expenseRequest);
        List<User> users = new ArrayList<>(userShares.keySet());

        Map<User, BigDecimal> allocations = splitStrategy.split(expense, users, userShares);
        addAllocationsToExpense(expense, allocations, expenseRequest);
    }

    private Map<User, BigDecimal> getUserShares(ExpenseRequest expenseRequest) {
        Map<User, BigDecimal> userShares = new HashMap<>();
        for (Map.Entry<Long, BigDecimal> entry : expenseRequest.getUserShares().entrySet()) {
            User user = getUserById(entry.getKey());
            userShares.put(user, entry.getValue());
        }
        return userShares;
    }

    private void addAllocationsToExpense(Expense expense, Map<User, BigDecimal> allocations, ExpenseRequest expenseRequest) {
        for (Map.Entry<User, BigDecimal> entry : allocations.entrySet()) {
            User user = entry.getKey();
            BigDecimal amountOwed = entry.getValue();

            BigDecimal percentage = BigDecimal.valueOf(0);
            if (expenseRequest.getSplitType() == SplitType.PERCENTAGE) {
                percentage = expenseRequest.getUserShares().get(user.getId());
            }

            expense.addAllocation(user, amountOwed, percentage);
        }
    }

    void reverseGroupBalance(Expense expense) {
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

    void validateExpenseRequest(ExpenseRequest expenseRequest) {
        BigDecimal totalAmount = expenseRequest.getTotalAmount();
        Map<Long, BigDecimal> userShares = expenseRequest.getUserShares();

        switch (expenseRequest.getSplitType()) {
            case AMOUNT:
                BigDecimal totalUserAmount = userShares.values().stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalUserAmount.compareTo(totalAmount) != 0) {
                    throw new InvalidExpenseException("The sum of user shares must equal the total amount.");
                }
                break;
            case PERCENTAGE:
                BigDecimal totalPercentage = userShares.values().stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
                    throw new InvalidExpenseException("The sum of user shares must equal 100%.");
                }
                break;
            case EQUALLY:
            case SHARES:
                break;
            default:
                throw new InvalidExpenseException("Invalid split type: " + expenseRequest.getSplitType());
        }
    }

    private void validateUserSplitsBelongToGroup(Group group, Map<Long, BigDecimal> userSplits) {
        for (Long userId : userSplits.keySet()) {
            if (!groupMembershipRepository.existsByGroupAndUser_Id(group, userId)) {
                throw new ActionIsNotAllowedException("User with ID " + userId + " is not a member of the group.");
            }
        }
    }
}