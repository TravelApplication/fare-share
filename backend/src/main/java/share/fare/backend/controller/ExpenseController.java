package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.fare.backend.util.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {
//    private final ExpenseService expenseService;
//
//    @GetMapping
//    public ResponseEntity<PaginatedResponse<ExpenseResponse>> getExpensesForGroup(
//            @PathVariable Long groupId,
//            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        Page<ExpenseResponse> expenses = expenseService.getExpensesForGroup(groupId, pageable);
//        return ResponseEntity.ok(new PaginatedResponse<>(expenses));
//    }
}
