package share.fare.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import share.fare.backend.dto.request.GroupRequest;
import share.fare.backend.dto.response.GroupResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.GroupService;
import share.fare.backend.util.PaginatedResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest groupRequest,
                                                     @AuthenticationPrincipal User user) {
        GroupResponse response = groupService.createGroup(groupRequest, user.getId());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<GroupResponse>> getAllGroups(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new PaginatedResponse<>(groupService.getAllGroups(pageable)));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupRequest groupRequest) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, groupRequest));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    // move it to separate service / controller for group memberships
//    @PostMapping("/{groupId}/members")
//    public ResponseEntity<GroupResponse> addMemberToGroup(
//            @PathVariable Long groupId,
//            @RequestParam Long userId,
//            @RequestParam(defaultValue = "MEMBER") GroupRole role) {
//        return ResponseEntity.ok(groupService.addMemberToGroup(groupId, userId, role));
//        return null;
//    }
}