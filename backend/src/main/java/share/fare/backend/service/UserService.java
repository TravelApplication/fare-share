package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.request.UserRequest;
import share.fare.backend.dto.response.UserResponse;
import share.fare.backend.entity.User;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.mapper.UserMapper;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.util.Notification;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    public UserResponse getUser(Long userId) {
        notificationService.sendNotificationToUser(userId, Notification.builder()
                .message("ktos sie o ciebie pyta").build());
        return UserMapper.toResponse(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));
    }

    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return UserMapper.toResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
