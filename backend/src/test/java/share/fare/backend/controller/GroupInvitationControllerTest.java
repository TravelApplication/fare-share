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
import share.fare.backend.entity.*;
import share.fare.backend.repository.GroupInvitationRepository;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GroupInvitationControllerTest {

    private static final String URI = "/api/v1/group-invitations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupInvitationRepository groupInvitationRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    private User testUser;
    private User testSecondUser;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        groupInvitationRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();

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

        testGroup = Group.builder()
                .name("Test Group")
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        testGroup = groupRepository.save(testGroup);
    }

    @AfterEach
    void cleanUp() {
        groupInvitationRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void sendGroupInvitationTest() throws Exception {
        GroupMembership groupMembership = GroupMembership.builder()
                .group(testGroup)
                .user(testUser)
                .joinedAt(LocalDate.now())
                .role(GroupRole.OWNER)
                .build();
        groupMembershipRepository.save(groupMembership);

        mockMvc.perform(post(URI + "/send")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .param("receiverId", String.valueOf(testSecondUser.getId()))
                        .param("groupId", String.valueOf(testGroup.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender.id").value(testUser.getId()))
                .andExpect(jsonPath("$.receiver.id").value(testSecondUser.getId()))
                .andExpect(jsonPath("$.groupId").value(testGroup.getId()));
    }

    @Test
    void sendGroupInvitationToYourselfTest() throws Exception {
        mockMvc.perform(post(URI + "/send")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .param("receiverId", String.valueOf(testUser.getId()))
                        .param("groupId", String.valueOf(testGroup.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You cannot send a group invitation to yourself."));
    }

    @Test
    void sendGroupInvitationAlreadyExistsTest() throws Exception {
        GroupInvitation invitation = GroupInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .group(testGroup)
                .createdAt(LocalDateTime.now())
                .build();

        groupInvitationRepository.save(invitation);

        mockMvc.perform(post(URI + "/send")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .param("receiverId", String.valueOf(testSecondUser.getId()))
                        .param("groupId", String.valueOf(testGroup.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Group invitation already exists."))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/group-invitations/send"));
    }

    @Test
    void sendGroupInvitationReceiverNotFoundTest() throws Exception {
        Long nonExistingReceiverId = testSecondUser.getId() + 10;
        mockMvc.perform(post(URI + "/send")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .param("receiverId", String.valueOf(nonExistingReceiverId))
                        .param("groupId", String.valueOf(testGroup.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID " + nonExistingReceiverId + " not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/group-invitations/send"));
    }

    @Test
    void sendGroupInvitationGroupNotFoundTest() throws Exception {
        Long nonExistingGroupId = testGroup.getId() + 10;
        mockMvc.perform(post(URI + "/send")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .param("receiverId", String.valueOf(testSecondUser.getId()))
                        .param("groupId", String.valueOf(nonExistingGroupId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendGroupInvitationSenderNotInGroupTest() throws Exception {
        mockMvc.perform(post(URI + "/send")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .param("receiverId", String.valueOf(testSecondUser.getId()))
                        .param("groupId", String.valueOf(testGroup.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Sender does not belong to group."));
    }

    @Test
    void acceptGroupInvitationTest() throws Exception {
        GroupInvitation invitation = GroupInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .group(testGroup)
                .createdAt(LocalDateTime.now())
                .build();

        GroupInvitation savedInvitation = groupInvitationRepository.save(invitation);

        mockMvc.perform(post(URI + "/accept/" + savedInvitation.getId())
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void acceptNonExistingGroupInvitationTest() throws Exception {
        mockMvc.perform(post(URI + "/accept/1")
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Group invitation not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/group-invitations/accept/1"));
    }

    @Test
    void rejectGroupInvitationTest() throws Exception {
        GroupInvitation invitation = GroupInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .group(testGroup)
                .createdAt(LocalDateTime.now())
                .build();

        GroupInvitation savedInvitation = groupInvitationRepository.save(invitation);

        mockMvc.perform(delete(URI + "/reject/" + savedInvitation.getId())
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNoContent());

        assertFalse(groupInvitationRepository.existsById(savedInvitation.getId()), "Invitation was not deleted");
    }

    @Test
    void rejectNonExistingGroupInvitationTest() throws Exception {
        mockMvc.perform(delete(URI + "/reject/1")
                        .with(user(testSecondUser))
                        .principal(() -> String.valueOf(testSecondUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Group invitation not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/group-invitations/reject/1"));
    }

    @Test
    void getSentGroupInvitationsTest() throws Exception {
        GroupInvitation invitation = GroupInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .group(testGroup)
                .createdAt(LocalDateTime.now())
                .build();

        groupInvitationRepository.save(invitation);

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
    void getReceivedGroupInvitationsTest() throws Exception {
        GroupInvitation invitation = GroupInvitation.builder()
                .sender(testUser)
                .receiver(testSecondUser)
                .group(testGroup)
                .createdAt(LocalDateTime.now())
                .build();

        groupInvitationRepository.save(invitation);

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
