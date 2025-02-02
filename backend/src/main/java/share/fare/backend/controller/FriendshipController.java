package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.entity.User;
import share.fare.backend.service.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<List<Long>> getAllFriendIds(@AuthenticationPrincipal User user) {
        List<Long> friendIds = friendshipService.getAllFriendships(user.getId());
        return ResponseEntity.ok(friendIds);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFriendship(@AuthenticationPrincipal User user, @RequestParam Long friendId) {
        friendshipService.deleteFriendship(user.getId(), friendId);
        return ResponseEntity.noContent().build();
    }
}
