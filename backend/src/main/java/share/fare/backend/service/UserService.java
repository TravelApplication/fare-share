package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
