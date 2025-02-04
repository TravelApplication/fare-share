package share.fare.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.response.FriendInvitationResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.FriendInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friend-invitations")
public class FriendInvitationController {

    private final FriendInvitationService friendInvitationService;

    @Autowired
    public FriendInvitationController(FriendInvitationService friendInvitationService) {
        this.friendInvitationService = friendInvitationService;
    }

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<FriendInvitationResponse> sendFriendInvitation(
            @AuthenticationPrincipal User user,
            @PathVariable Long receiverId) {
        FriendInvitationResponse response = friendInvitationService.sendFriendInvitation(user.getId(), receiverId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/accept/{invitationId}")
    public ResponseEntity<Void> acceptFriendInvitation(@PathVariable Long invitationId, @AuthenticationPrincipal User user) {
        friendInvitationService.acceptFriendInvitation(invitationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reject/{invitationId}")
    public ResponseEntity<Void> rejectFriendInvitation(@PathVariable Long invitationId, @AuthenticationPrincipal User user) {
        friendInvitationService.rejectFriendInvitation(invitationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sent")
    public ResponseEntity<List<FriendInvitationResponse>> getSentFriendInvitations(
            @AuthenticationPrincipal User user) {

        List<FriendInvitationResponse> responses = friendInvitationService.getSentFriendInvitations(user.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/received")
    public ResponseEntity<List<FriendInvitationResponse>> getReceivedFriendInvitations(
            @AuthenticationPrincipal User user) {

        List<FriendInvitationResponse> responses = friendInvitationService.getReceivedFriendInvitations(user.getId());
        return ResponseEntity.ok(responses);
    }
}
