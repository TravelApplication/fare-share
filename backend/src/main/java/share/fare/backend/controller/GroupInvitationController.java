package share.fare.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.response.GroupInvitationResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.GroupInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group-invitations")
public class GroupInvitationController {

    private final GroupInvitationService groupInvitationService;

    @Autowired
    public GroupInvitationController(GroupInvitationService groupInvitationService) {
        this.groupInvitationService = groupInvitationService;
    }

    @PostMapping("/send")
    public ResponseEntity<GroupInvitationResponse> sendGroupInvitation(
            @AuthenticationPrincipal User user,
            @RequestParam Long receiverId,
            @RequestParam Long groupId) {

        GroupInvitationResponse response = groupInvitationService.sendGroupInvitation(user.getId(), receiverId, groupId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept/{invitationId}")
    public ResponseEntity<Void> acceptGroupInvitation(@PathVariable Long invitationId, @AuthenticationPrincipal User user) {
        groupInvitationService.acceptGroupInvitation(invitationId, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reject/{invitationId}")
    public ResponseEntity<Void> rejectGroupInvitation(@PathVariable Long invitationId, @AuthenticationPrincipal User user) {
        groupInvitationService.rejectGroupInvitation(invitationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sent")
    public ResponseEntity<List<GroupInvitationResponse>> getSentGroupInvitations(
            @AuthenticationPrincipal User user) {

        List<GroupInvitationResponse> responses = groupInvitationService.getSentGroupInvitations(user.getId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/received")
    public ResponseEntity<List<GroupInvitationResponse>> getReceivedGroupInvitations(
            @AuthenticationPrincipal User user) {

        List<GroupInvitationResponse> responses = groupInvitationService.getReceivedGroupInvitations(user.getId());
        return ResponseEntity.ok(responses);
    }
}
