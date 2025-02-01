package share.fare.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.VoteRequest;
import share.fare.backend.dto.response.VoteResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.VoteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/activities/{activityId}/votes")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponse> addVote(
            @PathVariable Long groupId,
            @PathVariable Long activityId,
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal User user) {
        VoteResponse response = voteService.addVote(activityId, user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VoteResponse>> getVotesForActivity(
            @PathVariable Long groupId,
            @PathVariable Long activityId) {
        List<VoteResponse> votes = voteService.getVotesForActivity(activityId);
        return ResponseEntity.ok(votes);
    }

    @PutMapping("/{voteId}")
    public ResponseEntity<VoteResponse> updateVote(
            @PathVariable Long groupId,
            @PathVariable Long activityId,
            @PathVariable Long voteId,
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal User user) {
        VoteResponse response = voteService.updateVote(voteId, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{voteId}")
    public ResponseEntity<Void> deleteVote(
            @PathVariable Long groupId,
            @PathVariable Long activityId,
            @PathVariable Long voteId,
            @AuthenticationPrincipal User user) {
        voteService.deleteVote(voteId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
