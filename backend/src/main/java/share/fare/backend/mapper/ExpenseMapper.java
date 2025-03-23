package share.fare.backend.mapper;

import share.fare.backend.dto.request.ExpenseRequest;
import share.fare.backend.dto.response.ExpenseResponse;
import share.fare.backend.entity.Expense;
import share.fare.backend.entity.ExpenseAllocation;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.User;

import java.util.stream.Collectors;

public class ExpenseMapper {
    public static Expense toEntity(ExpenseRequest expenseRequest, Group group, User paidByUser) {
        if (expenseRequest == null) {
            return null;
        }

        return Expense.builder()
                .group(group)
                .paidByUser(paidByUser)
                .description(expenseRequest.getDescription())
                .totalAmount(expenseRequest.getTotalAmount())
                .splitType(expenseRequest.getSplitType())
                .build();
    }

    public static ExpenseResponse toResponse(Expense expense) {
        if (expense == null) {
            return null;
        }

        return ExpenseResponse.builder()
                .id(expense.getId())
                .groupId(expense.getGroup() != null ? expense.getGroup().getId() : null)
                .paidByUserId(expense.getPaidByUser() != null ? expense.getPaidByUser().getId() : null)
                .description(expense.getDescription())
                .totalAmount(expense.getTotalAmount())
                .splitType(expense.getSplitType())
                .userShares(expense.getExpenseAllocations().stream()
                        .collect(Collectors.toMap(
                                allocation -> allocation.getUser().getId(),
                                ExpenseAllocation::getAmountOwed
                        )))
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
