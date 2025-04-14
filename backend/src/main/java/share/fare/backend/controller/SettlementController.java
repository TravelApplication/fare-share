package share.fare.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.SettlementRequest;
import share.fare.backend.dto.response.SettlementResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.SettlementService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/settlements")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    @PostMapping
    public ResponseEntity<SettlementResponse> createSettlement(@RequestBody @Valid SettlementRequest request) {
        SettlementResponse response = settlementService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SettlementResponse>> getSettlement(@PathVariable Long groupId) {
        List<SettlementResponse> response = settlementService.getGroupPayments(groupId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{settlementId}")
    public ResponseEntity<SettlementResponse> updateSettlement(@RequestBody @Valid SettlementRequest request,
                                                               @AuthenticationPrincipal User user,
                                                               @PathVariable Long groupId,
                                                               @PathVariable Long settlementId) {
        SettlementResponse response = settlementService.updateSettlement(settlementId, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{settlementId}")
    public ResponseEntity<Void> deleteSettlement(@PathVariable Long groupId,
                                                 @PathVariable Long settlementId,
                                                 @AuthenticationPrincipal User user) {
        settlementService.deleteSettlement(settlementId, user);
        return ResponseEntity.noContent().build();
    }
}
