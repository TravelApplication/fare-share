package share.fare.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.UserInfoRequest;
import share.fare.backend.dto.response.UserInfoResponse;
import share.fare.backend.dto.response.UserResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.UserInfoService;
import share.fare.backend.util.PaginatedResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-info")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("/search/top8")
    public ResponseEntity<List<UserInfoResponse>> searchTop8Users(@RequestParam String name) {
        return ResponseEntity.ok(userInfoService.searchTop8Users(name));
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<UserInfoResponse>> searchUsersPaginated(
            @RequestParam String name,
            @PageableDefault(size = 20, sort = "firstName", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new PaginatedResponse<>(userInfoService.searchUsersPaginated(name, pageable)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userInfoService.getUserProfile(userId));
    }

    @PutMapping
    public ResponseEntity<UserInfoResponse> updateUserInfo(@AuthenticationPrincipal User user,
                                                            @RequestBody @Valid UserInfoRequest request) {
        return ResponseEntity.ok(userInfoService.addOrUpdateUserInfo(request, user.getId()));
    }
}