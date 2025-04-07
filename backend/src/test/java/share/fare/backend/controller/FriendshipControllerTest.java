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
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.repository.FriendshipRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FriendshipControllerTest {

    private static final String URI = "/api/v1/friendships";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    private User testUser;
    private User testSecondUser;

    @BeforeEach
    void setUp() {
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
        friendshipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllFriendsTest() throws Exception {
        createFriendship();

        mockMvc.perform(get(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(testSecondUser.getId()))
                .andExpect(jsonPath("$.[0].email").value(testSecondUser.getEmail()));
    }

    @Test
    void deleteFriendshipTest() throws Exception {
        createFriendship();

        mockMvc.perform(delete(URI + "/" + testSecondUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistingFriendshipTest() throws Exception {
        Long nonExistingFriendId = testSecondUser.getId() + 10;
        mockMvc.perform(delete(URI + "/" + nonExistingFriendId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Friendship not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/friendships/" + nonExistingFriendId));
    }

    @Test
    void getAllFriendsWhenNoFriendsTest() throws Exception {
        mockMvc.perform(get(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    private void createFriendship(){
        FriendshipId friendshipId = new FriendshipId(testUser.getId(), testSecondUser.getId());
        Friendship friendship = Friendship.builder()
                .id(friendshipId)
                .user1(testUser)
                .user2(testSecondUser)
                .createdAt(LocalDateTime.now())
                .build();
        friendshipRepository.save(friendship);
    }
}
