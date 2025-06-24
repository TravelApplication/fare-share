package share.fare.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import share.fare.backend.entity.*;
import share.fare.backend.repository.ChatMessageRepository;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GroupChatControllerTest {
    private static final String URI = "/api/v1/group/1/chat";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MockMvc mockMvc;

    private User testUser;
    private User testSecondUser;
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

        Group group = Group.builder()
                .name("group")
                .description("group")
                .createdBy(testUser)
                .build();
        testGroup = groupRepository.save(group);

        User secondUser = User.builder()
                .role(Role.USER)
                .email("user2@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testSecondUser = userRepository.save(secondUser);

        GroupMembership groupMembership = GroupMembership.builder()
                .group(testGroup)
                .user(testUser)
                .build();
        groupMembershipRepository.save(groupMembership);

        ChatMessage message = ChatMessage.builder()
                .groupId(testGroup.getId())
                .timestamp(LocalDateTime.now())
                .content("Hello")
                .senderEmail(testUser.getEmail())
                .senderId(testUser.getId())
                .build();
        chatMessageRepository.save(message);
    }

    @Test
    public void getRecentMessagesSuccessTest() throws Exception {
        mockMvc.perform(get("/api/v1/group/{groupId}/chat", testGroup.getId())
                        .param("page", "0")
                        .param("size", "20")
                        .with(user(testUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].content").value("Hello"))
                .andExpect(jsonPath("$.content[0].senderId").value(testUser.getId()));
    }

    @Test
    public void getRecentMessagesGroupDoesNotExisitTest() throws Exception {
        mockMvc.perform(get("/api/v1/group/{groupId}/chat", testGroup.getId() + 5)
                        .param("page", "0")
                        .param("size", "20")
                        .with(user(testUser))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRecentMessagesUserNotInGroupTest() throws Exception {
        mockMvc.perform(get("/api/v1/group/{groupId}/chat", testGroup.getId())
                        .param("page", "0")
                        .param("size", "20")
                        .with(user(testSecondUser))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User " + testSecondUser.getId() + " is not in group " + testGroup.getName()));
    }
}
