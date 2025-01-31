package share.fare.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.dto.request.UserRequest;
import share.fare.backend.dto.response.UserResponse;
import share.fare.backend.service.UserService;
import share.fare.backend.util.PaginatedResponse;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    // ????
//    @GetMapping("/user")
//    public String getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
//        if (principal != null) {
//            return "User attributes: " + principal.getAttributes().toString();
//        }
//        return "No user logged in.";
//    }

    @GetMapping
    public PaginatedResponse<UserResponse> getUsers(Pageable pageable) {
        return new PaginatedResponse<>(userService.getUsers(pageable));
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
}
