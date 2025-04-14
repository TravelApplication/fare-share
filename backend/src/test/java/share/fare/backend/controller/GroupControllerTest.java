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
import share.fare.backend.dto.request.GroupRequest;
import share.fare.backend.entity.*;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GroupControllerTest {

    private static final String URI = "/api/v1/groups";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

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
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    void createGroupTest() throws Exception {
        String groupName = "Test Group";
        GroupRequest request = GroupRequest.builder()
                .name(groupName)
                .createdByUserId(testUser.getId())
                .description("Test description")
                .build();

        mockMvc.perform(post(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(groupName))
                .andExpect(jsonPath("$.createdByUserId").value(testUser.getId()));
    }

    @Test
    void createGroupStartDateInThePastTest() throws Exception {
        GroupRequest request = GroupRequest.builder()
                .name("Test Group")
                .createdByUserId(testUser.getId())
                .tripStartDate(LocalDate.now().minusDays(2))
                .build();

        mockMvc.perform(post(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.tripStartDate").value("Trip start date must be in the present or future"));
    }

    @Test
    void createGroupEndDateInThePastTest() throws Exception {
        GroupRequest request = GroupRequest.builder()
                .name("Test Group")
                .createdByUserId(testUser.getId())
                .tripEndDate(LocalDate.now().minusDays(2))
                .build();

        mockMvc.perform(post(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.tripEndDate").value("Trip end date must be in the future"));
    }

    @Test
    void createGroupStartDateAfterEndDateTest() throws Exception {
        GroupRequest request = GroupRequest.builder()
                .name("Test Group")
                .createdByUserId(testUser.getId())
                .tripStartDate(LocalDate.now().plusDays(5))
                .tripEndDate(LocalDate.now().plusDays(2))
                .build();

        mockMvc.perform(post(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Trip start date must be before end date"));
    }

    @Test
    void createGroupWithoutNameTest() throws Exception {
        GroupRequest request = GroupRequest.builder()
                .description("Test description")
                .build();

        mockMvc.perform(post(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.name").value("Group name is required"));
    }

    @Test
    void createGroupInvalidUrlTest() throws Exception {
        GroupRequest request = GroupRequest.builder()
                .name("Test Group")
                .createdByUserId(testUser.getId())
                .groupImageUrl("error")
                .description("Test description")
                .build();

        mockMvc.perform(post(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.groupImageUrl").value("Group image URL must be valid"));
    }

    @Test
    void updateGroupTest() throws Exception {
        String updatedGroupName = "Updated Test Group";
        String updatedDescription = "Updated Test Description";
        GroupRequest request = GroupRequest.builder()
                .name(updatedGroupName)
                .description(updatedDescription)
                .createdByUserId(testUser.getId())
                .build();

        mockMvc.perform(put(URI + "/" + testGroup.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedGroupName))
                .andExpect(jsonPath("$.description").value(updatedDescription));
    }

    @Test
    void updateNonExistingGroupTest() throws Exception {
        Long nonExisitingGroupId = testGroup.getId() + 10;
        GroupRequest request = GroupRequest.builder()
                .name("Updated Test Group")
                .createdByUserId(testUser.getId())
                .description("Updated description")
                .build();

        mockMvc.perform(put(URI + "/" + nonExisitingGroupId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGroupTest() throws Exception {
        mockMvc.perform(delete(URI + "/" + testGroup.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistingGroupTest() throws Exception {
        Long nonExisitingGroupId = testGroup.getId() + 10;
        mockMvc.perform(delete(URI + "/" + nonExisitingGroupId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void getGroupTest() throws Exception {
        mockMvc.perform(get(URI + "/" + testGroup.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testGroup.getName()))
                .andExpect(jsonPath("$.description").value(testGroup.getDescription()));
    }

    @Test
    void getNonExistingGroupTest() throws Exception {
        Long nonExisitingGroupId = testGroup.getId() + 10;
        mockMvc.perform(get(URI + "/" + nonExisitingGroupId)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllGroupsTest() throws Exception {
        mockMvc.perform(get(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getGroupsForUserTest() throws Exception {
        mockMvc.perform(get(URI + "/user-groups")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(testGroup.getId()))
                .andDo(print());
    }
}
