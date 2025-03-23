package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.fare.backend.dto.request.TransactionRequest;
import share.fare.backend.service.GroupBalanceService;

import java.util.List;

@RequestMapping("/api/v1/groups/{groupId}/balance")
@RequiredArgsConstructor
@RestController
public class GroupBalanceController {
    private final GroupBalanceService groupBalanceService;

    @GetMapping
    public ResponseEntity<List<TransactionRequest>> getAllTransactions(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupBalanceService.getTransactions(groupId));
    }
}
