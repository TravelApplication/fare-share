package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.response.UserGeneralResponse;
import share.fare.backend.entity.User;
import share.fare.backend.service.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<List<UserGeneralResponse>> getAllFriendIds(@AuthenticationPrincipal User user) {
        List<UserGeneralResponse> friendIds = friendshipService.findFriendsByUserId(user.getId());
        return ResponseEntity.ok(friendIds);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriendship(@AuthenticationPrincipal User user,
                                                 @PathVariable Long friendId) {
        friendshipService.deleteFriendship(user.getId(), friendId);
        return ResponseEntity.noContent().build();
    }
}
