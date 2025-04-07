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
import share.fare.backend.dto.request.VoteRequest;
import share.fare.backend.dto.response.VoteResponse;
import share.fare.backend.entity.*;
import share.fare.backend.repository.*;
import share.fare.backend.service.VoteService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VoteControllerTest {

    private static final String URI = "/api/v1/groups/{groupId}/activities/{activityId}/votes";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private GroupMembershipRepository gmRepository;

    @Autowired
    private VoteService voteService;

    private User testUser;
    private User anotherUser;
    private Group testGroup;
    private Activity testActivity;
    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .role(Role.USER)
                .email("test@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(user);

        User another = User.builder()
                .role(Role.USER)
                .email("another@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        anotherUser = userRepository.save(another);

        Group group = Group.builder()
                .name("Test Group")
                .description("Test group description")
                .createdAt(LocalDateTime.now())
                .createdBy(testUser)
                .build();
        testGroup = groupRepository.save(group);

        Activity activity = Activity.builder()
                .name("Test Activity")
                .description("Test activity description")
                .group(testGroup)
                .createdAt(LocalDateTime.now())
                .build();
        testActivity = activityRepository.save(activity);

        GroupMembership groupMembership = GroupMembership.builder()
                .group(testGroup)
                .user(testUser)
                .role(GroupRole.OWNER)
                .joinedAt(LocalDate.now())
                .build();

        groupMembershipRepository.save(groupMembership);
    }

    @AfterEach
    void cleanUp() {
        voteRepository.deleteAll();
        activityRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
        groupMembershipRepository.deleteAll();
    }

    @Test
    void addVoteTest() throws Exception {
        VoteRequest voteRequest = VoteRequest.builder()
                .voteType(VoteType.FOR)
                .build();

        mockMvc.perform(post(URI, testGroup.getId(), testActivity.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voteRequest))
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.voteType").value(VoteType.FOR.toString()));
    }

    @Test
    void addVoteNotGroupMemberTest() throws Exception {
        VoteRequest voteRequest = VoteRequest.builder()
                .voteType(VoteType.FOR)
                .build();

        mockMvc.perform(post(URI, testGroup.getId(), testActivity.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voteRequest))
                        .with(user(anotherUser))
                        .principal(() -> String.valueOf(anotherUser.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with ID: " + anotherUser.getId() +" is not a member of the group"));
    }

    @Test
    void addVoteAlreadyVotedTest() throws Exception {
        Vote vote = Vote.builder()
                .voteType(VoteType.FOR)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .activity(testActivity)
                .build();
        voteRepository.save(vote);

        VoteRequest voteRequest = VoteRequest.builder()
                .voteType(VoteType.FOR)
                .build();

        mockMvc.perform(post(URI, testGroup.getId(), testActivity.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voteRequest))
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User has already voted on this activity"));
    }

    @Test
    void addVoteWithoutAuthTest() throws Exception {
        VoteRequest voteRequest = VoteRequest.builder()
                .voteType(VoteType.FOR)
                .build();

        mockMvc.perform(post(URI, testGroup.getId(), testActivity.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getVotesForActivityTest() throws Exception {
        VoteRequest voteRequest = VoteRequest.builder()
                .voteType(VoteType.FOR)
                .build();
        voteService.addVote(testActivity.getId(), testUser.getId(), voteRequest);

        mockMvc.perform(get(URI, testGroup.getId(), testActivity.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(testUser.getId()))
                .andExpect(jsonPath("$[0].voteType").value(VoteType.FOR.toString()));
    }

    @Test
    void updateVoteTest() throws Exception {
        VoteRequest initialVote = VoteRequest.builder()
                .voteType(VoteType.FOR)
                .build();
        VoteResponse voteResponse = voteService.addVote(testActivity.getId(), testUser.getId(), initialVote);

        VoteRequest updatedVote = VoteRequest.builder()
                .voteType(VoteType.AGAINST)
                .build();
        mockMvc.perform(put(URI + "/{voteId}", testGroup.getId(), testActivity.getId(), voteResponse.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedVote))
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteType").value(VoteType.AGAINST.toString()));
    }

    @Test
    void deleteVoteTest() throws Exception {
        VoteRequest voteRequest = new VoteRequest(VoteType.FOR);
        VoteResponse voteResponse = voteService.addVote(testActivity.getId(), testUser.getId(), voteRequest);

        mockMvc.perform(delete(URI + "/{voteId}", testGroup.getId(), testActivity.getId(), voteResponse.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistentVoteTest() throws Exception {
        Long nonExistentVoteId = testActivity.getId() + 10;
        mockMvc.perform(delete(URI + "/{voteId}", testGroup.getId(), testActivity.getId(), nonExistentVoteId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Vote not found with ID: " + nonExistentVoteId));
    }
}
