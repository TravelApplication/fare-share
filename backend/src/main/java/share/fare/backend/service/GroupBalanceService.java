package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.response.GroupBalanceResponse;
import share.fare.backend.dto.response.TransactionResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.mapper.GroupBalanceMapper;
import share.fare.backend.repository.GroupBalanceRepository;
import share.fare.backend.repository.GroupRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupBalanceService {
    private final GroupBalanceRepository groupBalanceRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void updateBalances(Expense expense) {
        Group group = expense.getGroup();
        User paidByUser = expense.getPaidByUser();
        BigDecimal totalAmount = expense.getTotalAmount();

        GroupBalance paidByBalance = groupBalanceRepository.findByGroupAndUser(group, paidByUser)
                .orElse(new GroupBalance(group, paidByUser, BigDecimal.ZERO));
        paidByBalance.setBalance(paidByBalance.getBalance().add(totalAmount));
        groupBalanceRepository.save(paidByBalance);

        for (ExpenseAllocation allocation : expense.getExpenseAllocations()) {
            User user = allocation.getUser();
            BigDecimal amountOwed = allocation.getAmountOwed();

            GroupBalance userBalance = groupBalanceRepository.findByGroupAndUser(group, user)
                    .orElse(new GroupBalance(group, user, BigDecimal.ZERO));
            userBalance.setBalance(userBalance.getBalance().subtract(amountOwed));
            groupBalanceRepository.save(userBalance);
        }
    }

    public List<GroupBalanceResponse> getBalances(Long groupIp) {
        Group group = groupRepository.findById(groupIp).orElseThrow(() -> new GroupNotFoundException(groupIp));
        List<GroupBalance> balances = groupBalanceRepository.findByGroup(group);
        return balances.stream().map(GroupBalanceMapper::toResponse).toList();
    }

    public List<TransactionResponse> getTransactions(Long groupIp) {
        Group group = groupRepository.findById(groupIp).orElseThrow(() -> new GroupNotFoundException(groupIp));
        List<GroupBalance> balances = groupBalanceRepository.findByGroup(group);
        List<TransactionResponse> transactions = new ArrayList<>();

        List<GroupBalance> debtors = balances.stream()
                .filter(balance -> balance.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .toList();

        List<GroupBalance> creditors = balances.stream()
                .filter(balance -> balance.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        for (GroupBalance debtor : debtors) {
            BigDecimal debt = debtor.getBalance().abs();

            for (GroupBalance creditor : creditors) {
                if (debt.compareTo(BigDecimal.ZERO) == 0) break;

                BigDecimal credit = creditor.getBalance();
                BigDecimal amountToTransfer = debt.min(credit);

                transactions.add(new TransactionResponse(
                        debtor.getUser().getId(),
                        creditor.getUser().getId(),
                        amountToTransfer
                ));

                debt = debt.subtract(amountToTransfer);
                creditor.setBalance(creditor.getBalance().subtract(amountToTransfer));
            }
        }

        return transactions;
    }
}
