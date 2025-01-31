package share.fare.backend.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (userRepository.count() == 0) {
            User user1 = User.builder()
                    .email("user@example.com")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .createdAt(LocalDate.now())
                    .build();

            User user2 = User.builder()
                    .email("user2@example.com")
                    .password(passwordEncoder.encode("password2"))
                    .role(Role.USER)
                    .createdAt(LocalDate.now())
                    .build();

            User user3 = User.builder()
                    .email("user3@example.com")
                    .password(passwordEncoder.encode("password3"))
                    .role(Role.USER)
                    .createdAt(LocalDate.now())
                    .build();

            userRepository.saveAll(List.of(user1, user2, user3));
        }
    }
}
