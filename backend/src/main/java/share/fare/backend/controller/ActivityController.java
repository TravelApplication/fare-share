package share.fare.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.ActivityRequest;
import share.fare.backend.dto.response.ActivityResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.ActivityService;
import share.fare.backend.util.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(
            @PathVariable Long groupId,
            @Valid @RequestBody ActivityRequest request,
            @AuthenticationPrincipal User user) {
        ActivityResponse response = activityService.createActivity(request, user.getId(), groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable Long groupId,
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityRequest request) {
        ActivityResponse response = activityService.updateActivity(request, activityId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long groupId,
            @PathVariable Long activityId) {
        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ActivityResponse>> getActivitiesForGroup(
            @PathVariable Long groupId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ActivityResponse> activities = activityService.getActivitiesForGroup(groupId, pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(activities));
    }
}
