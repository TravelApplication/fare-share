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
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.service.GroupMembershipService;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GroupMembershipControllerTest {

    private static final String URI = "/api/v1/groups/{groupId}/members";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private GroupMembershipService groupMembershipService;

    private User testUser;
    private User newUser;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .role(Role.USER)
                .email("test@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(user);

        User anotherUser = User.builder()
                .role(Role.USER)
                .email("anotheruser@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        newUser = userRepository.save(anotherUser);

        Group group = Group.builder()
                .name("test group")
                .description("test group description")
                .createdAt(LocalDateTime.now())
                .createdBy(testUser)
                .build();

        testGroup = groupRepository.save(group);

        groupMembershipService.addMemberToGroup(testGroup.getId(), testUser.getId(), GroupRole.OWNER, testUser.getId());
    }

    @AfterEach
    void cleanUp() {
        groupMembershipRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addMemberTest() throws Exception {
        mockMvc.perform(post(URI, testGroup.getId())
                        .param("userId", String.valueOf(newUser.getId()))
                        .param("role", "MEMBER")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(newUser.getId()))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void addMemberAlreadyIsInGroupTest() throws Exception {
        groupMembershipService.addMemberToGroup(testGroup.getId(), newUser.getId(), GroupRole.MEMBER, testUser.getId());
        mockMvc.perform(post(URI, testGroup.getId())
                        .param("userId", String.valueOf(newUser.getId()))
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User is already a member of this group"));
    }

    @Test
    void addNonExistingMemberTest() throws Exception {
        Long nonExistingUserId = newUser.getId() + 10;
        mockMvc.perform(post(URI, testGroup.getId())
                        .param("userId", String.valueOf(nonExistingUserId))
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID " + nonExistingUserId + " not found"));
    }

    @Test
    void addMemberWithoutAuthTest() throws Exception {
        mockMvc.perform(post(URI, testGroup.getId())
                        .param("userId", String.valueOf(newUser.getId()))
                        .param("role", "MEMBER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeMemberTest() throws Exception {
        groupMembershipService.addMemberToGroup(testGroup.getId(), newUser.getId(), GroupRole.MEMBER, testUser.getId());

        mockMvc.perform(delete(URI + "/{userId}", testGroup.getId(), newUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeOwnerTest() throws Exception {
        groupMembershipService.addMemberToGroup(testGroup.getId(), newUser.getId(), GroupRole.MEMBER, testUser.getId());

        mockMvc.perform(delete(URI + "/{userId}", testGroup.getId(), testUser.getId())
                        .with(user(newUser))
                        .principal(() -> String.valueOf(newUser.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Owner cannot be removed from the group"));
    }

    @Test
    void removeNonMemberTest() throws Exception {
        mockMvc.perform(delete(URI + "/{userId}", testGroup.getId(), newUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User is not a member of the group"));
    }

    @Test
    void removeNonExistingMemberTest() throws Exception {
        Long nonExistingUserId = testUser.getId() + 10;
        mockMvc.perform(delete(URI + "/{userId}", testGroup.getId(), nonExistingUserId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID " + nonExistingUserId + " not found"));
    }

    @Test
    void updateMemberRoleTest() throws Exception {
        groupMembershipService.addMemberToGroup(testGroup.getId(), newUser.getId(), GroupRole.MEMBER, testUser.getId());

        mockMvc.perform(put(URI + "/{userId}/role", testGroup.getId(), newUser.getId())
                        .param("role", "ADMIN")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void getGroupMembersTest() throws Exception {
        groupMembershipService.addMemberToGroup(testGroup.getId(), newUser.getId(), GroupRole.MEMBER, testUser.getId());

        mockMvc.perform(get(URI, testGroup.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(testUser.getId()))
                .andExpect(jsonPath("$[1].userId").value(newUser.getId()));
    }

    @Test
    void getGroupMembersWhenNoMembersTest() throws Exception {
        Group emptyGroup = Group.builder()
                .name("empty group")
                .description("group with no members")
                .createdAt(LocalDateTime.now())
                .createdBy(testUser)
                .build();
        groupRepository.save(emptyGroup);

        mockMvc.perform(get(URI, emptyGroup.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
