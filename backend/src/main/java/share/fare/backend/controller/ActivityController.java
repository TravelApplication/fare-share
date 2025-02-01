package share.fare.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.ActivityRequest;
import share.fare.backend.dto.response.ActivityResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.ActivityService;

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
}
