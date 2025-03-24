package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.ExpenseRequest;
import share.fare.backend.dto.response.ExpenseResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.ExpenseService;
import share.fare.backend.util.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<ExpenseResponse>> getExpensesForGroup(
            @PathVariable Long groupId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ExpenseResponse> expenses = expenseService.getExpensesForGroup(groupId, pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(expenses));
    }

    @PostMapping
    public ResponseEntity<String> addExpense(@RequestBody ExpenseRequest expenseRequest) {
        expenseService.addExpense(expenseRequest);
        return ResponseEntity.ok("Expense added successfully");
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteExpense(@RequestParam Long expenseId,
                                              @AuthenticationPrincipal User authenticatedUser) {
        expenseService.removeExpense(expenseId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<Void> updateExpense(@PathVariable Long expenseId,
                                              @RequestBody ExpenseRequest expenseRequest,
                                              @AuthenticationPrincipal User authenticatedUser) {
        expenseService.updateExpense(expenseId, expenseRequest, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
