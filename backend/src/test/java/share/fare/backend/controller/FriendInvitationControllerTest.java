package share.fare.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import share.fare.backend.entity.FriendInvitation;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.repository.FriendInvitationRepository;
import share.fare.backend.repository.FriendshipRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FriendInvitationControllerTest {
    private static final String URI = "/api/v1/friend-invitations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendInvitationRepository friendInvitationRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    private User testUser;
    private User testSecondUser;

    @BeforeEach
    void setUp() {
        friendInvitationRepository.deleteAll();
        User user = User.builder()
                .role(Role.USER)
                .email("test@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(user);

        User secondUser = User.builder()
                .role(Role.USER)
                .email("user2@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testSecondUser = userRepository.save(secondUser);
    }

    @AfterEach
    void cleanUp() {
        friendInvitationRepository.deleteAll();
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void sendFriendInvitationTest() throws Exception {
        mockMvc.perform(post(URI + "/send/" + testSecondUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender.id").value(testUser.getId()))
                .andExpect(jsonPath("$.receiver.id").value(testSecondUser.getId()));
    }

    @Test
    void sendFriendInvitationToYourselfTest() throws Exception {
        mockMvc.perform(post(URI + "/send/" + testUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You cannot send a friend invitation to yourself."));
    }

    @Test
    void sendFriendInvitationAlreadyExistsTest() throws Exception {
        FriendInvitation invitation = FriendInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .createdAt(LocalDateTime.now())
                .build();

        friendInvitationRepository.save(invitation);

        mockMvc.perform(post(URI + "/send/" + testSecondUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Friend invitation already exists."))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/friend-invitations/send/" + testSecondUser.getId()));
    }

    @Test
    void sendFriendInvitationReceiverNotFoundTest() throws Exception {
        Long nonExistingReceiverId = testSecondUser.getId() + 10;
        mockMvc.perform(post(URI + "/send/" + nonExistingReceiverId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID " + nonExistingReceiverId + " not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/friend-invitations/send/" + nonExistingReceiverId));
    }

    @Test
    void acceptFriendInvitationTest() throws Exception {
        FriendInvitation invitation = FriendInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .createdAt(LocalDateTime.now())
                .build();

        FriendInvitation savedInvitation =  friendInvitationRepository.save(invitation);

        mockMvc.perform(post(URI + "/accept/" + savedInvitation.getId())
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNoContent());

       assertTrue(friendshipRepository.existsById(new FriendshipId(testUser.getId(), testSecondUser.getId())), "Friendship was not created");
    }

    @Test
    void acceptNonExistingFriendInvitationTest() throws Exception {
        mockMvc.perform(post(URI + "/accept/" + 1L)
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Friend Invitation not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/friend-invitations/accept/1"));
    }

    @Test
    void rejectFriendInvitationTest() throws Exception {
        FriendInvitation invitation = FriendInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .createdAt(LocalDateTime.now())
                .build();

        FriendInvitation savedInvitation =  friendInvitationRepository.save(invitation);

        mockMvc.perform(delete(URI + "/reject/" + savedInvitation.getId())
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNoContent());

        assertFalse(friendshipRepository.existsById(new FriendshipId(testUser.getId(), testSecondUser.getId())), "Friendship was created after reject");
    }

    @Test
    void rejectNonExistingFriendInvitationTest() throws Exception {
        mockMvc.perform(delete(URI + "/reject/" + 1L)
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Friend Invitation not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/friend-invitations/reject/1"));
    }

    @Test
    void getSentFriendInvitationsTest() throws Exception {
        FriendInvitation invitation = FriendInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .createdAt(LocalDateTime.now())
                .build();

        friendInvitationRepository.save(invitation);

        mockMvc.perform(get(URI + "/sent")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].sender.id").value(testUser.getId()));

        mockMvc.perform(get(URI + "/received")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void getReceivedFriendInvitationsTest() throws Exception {
        FriendInvitation invitation = FriendInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .createdAt(LocalDateTime.now())
                .build();

        friendInvitationRepository.save(invitation);

        mockMvc.perform(get(URI + "/sent")
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        mockMvc.perform(get(URI + "/received")
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].sender.id").value(testUser.getId()));
    }
}
