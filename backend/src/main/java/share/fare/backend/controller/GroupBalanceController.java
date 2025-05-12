package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.fare.backend.dto.response.GroupBalanceResponse;
import share.fare.backend.dto.response.TransactionResponse;
import share.fare.backend.service.GroupBalanceService;

import java.util.List;

@RequestMapping("/api/v1/groups/{groupId}/balance")
@RequiredArgsConstructor
@RestController
public class GroupBalanceController {
    private final GroupBalanceService groupBalanceService;

    @GetMapping("/minimum-transactions")
    public ResponseEntity<List<TransactionResponse>> getMinTransactions(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupBalanceService.getMinimumTransactions(groupId));
    }

    @GetMapping("/balances")
    public ResponseEntity<List<GroupBalanceResponse>> getBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupBalanceService.getBalances(groupId));
    }
}
