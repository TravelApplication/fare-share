package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.entity.GroupRole;
import share.fare.backend.entity.User;
import share.fare.backend.service.GroupMembershipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMembershipController {
    private final GroupMembershipService groupMembershipService;

    @PostMapping
    public ResponseEntity<GroupMembershipResponse> addMember(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "MEMBER") GroupRole role,
            @AuthenticationPrincipal User currentUser) {
        GroupMembershipResponse response = groupMembershipService.addMemberToGroup(groupId, userId, role, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupMembershipService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<GroupMembershipResponse> updateMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam GroupRole role) {
        GroupMembershipResponse response = groupMembershipService.updateMemberRole(groupId, userId, role);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupMembershipResponse>> getGroupMembers(
            @PathVariable Long groupId) {
        List<GroupMembershipResponse> members = groupMembershipService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }
}