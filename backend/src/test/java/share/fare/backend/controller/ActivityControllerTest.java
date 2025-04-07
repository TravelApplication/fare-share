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
import share.fare.backend.dto.request.ActivityRequest;
import share.fare.backend.entity.*;
import share.fare.backend.repository.ActivityRepository;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ActivityControllerTest {
    private static final String URI = "/api/v1/groups/";

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
    private GroupMembershipRepository gmRepository;

    private User testUser;

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
                .name("test")
                .description("test")
                .createdAt(LocalDateTime.now())
                .createdBy(testUser)
                .build();

        testGroup = groupRepository.save(group);

        GroupMembership gm = GroupMembership.builder().group(group).user(testUser).build();
        gmRepository.save(gm);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
        gmRepository.deleteAll();
        activityRepository.deleteAll();
    }

    @Test
    void createActivityTest() throws Exception {
        String activityName = "Test Activity";
        ActivityRequest request = ActivityRequest.builder()
                .name(activityName)
                .build();
        mockMvc.perform(post(URI + testGroup.getId() + "/activities")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(activityName))
                .andExpect(jsonPath("$.groupId").value(testGroup.getId()));
    }

    @Test
    void createActivityWithoutNameTest() throws Exception {
        ActivityRequest request = ActivityRequest.builder()
                .location("Test Location")
                .build();
        mockMvc.perform(post(URI + testGroup.getId() + "/activities")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.name").value("Activity name is required"))
                .andExpect(jsonPath("$.numberOfErrors").value(1));
    }

    @Test
    void createActivityWithInvalidUrlTest() throws Exception {
        ActivityRequest request = ActivityRequest.builder()
                .name("Test Activity")
                .link("invalidlink")
                .build();
        mockMvc.perform(post(URI + testGroup.getId() + "/activities")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.link").value("Link must be a valid URL"))
                .andExpect(jsonPath("$.numberOfErrors").value(1));
    }

    @Test
    void createActivityWithInvalidGroupIdTest() throws Exception {
        Long invalidGroupId = testGroup.getId() + 10;
        ActivityRequest request = ActivityRequest.builder()
                .name("Test Activity")
                .build();
        mockMvc.perform(post(URI + invalidGroupId+ "/activities")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createActivityWithoutAuthTest() throws Exception {
        ActivityRequest request = ActivityRequest.builder()
                .name("Test Activity")
                .build();
        mockMvc.perform(post(URI + testGroup.getId() + "/activities")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateActivityTest() throws Exception {
        Activity activity = Activity.builder()
                .name("Test Activity")
                .group(testGroup)
                .build();
        Activity savedActivity = activityRepository.save(activity);

        String updatedActivityName = "New Test Activity";
        ActivityRequest request = ActivityRequest.builder()
                .name(updatedActivityName)
                .build();

        mockMvc.perform(put(URI + testGroup.getId() + "/activities/" + savedActivity.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedActivityName))
                .andExpect(jsonPath("$.groupId").value(testGroup.getId()));
    }

    @Test
    void updateNonExistingActivityTest() throws Exception {
        String updatedActivityName = "New Test Activity";
        ActivityRequest request = ActivityRequest.builder()
                .name(updatedActivityName)
                .build();

        mockMvc.perform(put(URI + testGroup.getId() + "/activities/" + 1L)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Activity with ID: 1 not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/groups/" + testGroup.getId() + "/activities/1"))
                .andDo(print());
    }

    @Test
    void deleteActivityTest() throws Exception {
        Activity activity = Activity.builder()
                .name("Test Activity")
                .group(testGroup)
                .build();
        Activity savedActivity = activityRepository.save(activity);

        mockMvc.perform(delete(URI + testGroup.getId() + "/activities/" + savedActivity.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void getActivitiesForGroupTest() throws Exception {
        mockMvc.perform(get(URI + testGroup.getId() + "/activities")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
