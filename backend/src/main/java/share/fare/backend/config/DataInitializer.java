package share.fare.backend.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import share.fare.backend.entity.*;
import share.fare.backend.repository.FriendInvitationRepository;
import share.fare.backend.repository.GroupInvitationRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FriendInvitationRepository friendInvitationRepository;
    private final GroupRepository groupRepository;
    private final GroupInvitationRepository groupInvitationRepository;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (userRepository.count() == 0) {
            User user1 = User.builder()
                    .email("user@example.com")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();

            User user2 = User.builder()
                    .email("user2@example.com")
                    .password(passwordEncoder.encode("password2"))
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            User user3 = User.builder()
                    .email("user3@example.com")
                    .password(passwordEncoder.encode("password3"))
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.saveAll(List.of(user1, user2, user3));

            FriendInvitation friendInvitation = FriendInvitation.builder()
                    .sender(user1)
                    .receiver(user2)
                    .build();

            friendInvitationRepository.save(friendInvitation);

            Group group = Group.builder()
                    .name("Paris 2025")
                    .description("QUI QUI!")
                    .createdBy(user1)
                    .tripStartDate(LocalDate.now().plusDays(10))
                    .tripEndDate(LocalDate.now().plusDays(20))
                    .build();

            groupRepository.save(group);

            GroupInvitation groupInvitation = GroupInvitation.builder()
                    .sender(user1)
                    .receiver(user2)
                    .group(group)
                    .build();

            groupInvitationRepository.save(groupInvitation);
        }
    }
}
