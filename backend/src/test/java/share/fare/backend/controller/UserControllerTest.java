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
import share.fare.backend.dto.request.UserRequest;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.repository.UserRepository;
import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    private static final String URI = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    private User testAdmin;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .role(Role.USER)
                .email("test@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(user);

        User admin = User.builder()
                .role(Role.ADMIN)
                .email("admin@test.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        testAdmin = userRepository.save(admin);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void getUserTest() throws Exception {
         mockMvc.perform(get(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.id").value(testUser.getId()))
                 .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void getUserWithoutAuthTest() throws Exception {
        mockMvc.perform(get(URI))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserTest() throws Exception {
        String newEmail = "newemail@test.com";
        UserRequest userRequest = new UserRequest(newEmail, "newpassword");

        mockMvc.perform(put(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));
    }

    @Test
    void updateUserWithTooShortPasswordTest() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .email("newemail@test.com")
                .password("new")
                .build();

        mockMvc.perform(put(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorDescription.password").value("Password must be at least 8 characters long"))
                .andExpect(jsonPath("$.numberOfErrors").value(1));
    }

    @Test
    void updateUserWithoutEmailTest() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .password("newpassword")
                .build();

        mockMvc.perform(put(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.numberOfErrors").value(1))
                .andExpect(jsonPath("$.errorDescription.email").value("Email is required"))
                .andDo(print());
    }

    @Test
    void updateUserWithoutPasswordTest() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .email("newemail@test.com")
                .build();

        mockMvc.perform(put(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.numberOfErrors").value(1))
                .andExpect(jsonPath("$.errorDescription.password").value("Password is required"))
                .andDo(print());
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete(URI)
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserWithoutAuthTest() throws Exception {
        mockMvc.perform(delete(URI))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUserAsAdminTest() throws Exception {
        mockMvc.perform(delete(URI + "/admin/" + testUser.getId())
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAdminAsUserTest() throws Exception {
        mockMvc.perform(delete(URI + "/admin/" + testAdmin.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserAsAdminTest() throws Exception {
        mockMvc.perform(get(URI + "/admin/" + testUser.getId())
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void getNonExistingUserAsAdminTest() throws Exception {
        Long nonExistingUserId = testUser.getId() + 10;
        mockMvc.perform(get(URI + "/admin/" + nonExistingUserId)
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID " + nonExistingUserId + " not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/users/admin/" + nonExistingUserId));
    }

    @Test
    void updateUserAsAdminTest() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .email("newemail@test.com")
                .password("newpassword")
                .build();
        mockMvc.perform(put(URI + "/admin/" + testUser.getId())
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(userRequest.getEmail()));
    }

    @Test
    void updateUserAsNotAdminTest() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .email("newemail@test.com")
                .password("newpassword")
                .build();
        mockMvc.perform(put(URI + "/admin/" + testUser.getId())
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateNonExistingUserAsAdminTest() throws Exception {
        Long nonExistingUserId = testUser.getId() + 10;
        UserRequest userRequest = UserRequest.builder()
                .email("newemail@test.com")
                .password("newpassword")
                .build();
        mockMvc.perform(put(URI + "/admin/" + nonExistingUserId)
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID " + nonExistingUserId +" not found"))
                .andExpect(jsonPath("$.errorDescription").value("uri=/api/v1/users/admin/" + nonExistingUserId));
    }

    @Test
    void getAllUsersDefaultTest() throws Exception {
        mockMvc.perform(get(URI + "/admin")
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    void getAllUsersPaginationTest() throws Exception {
        mockMvc.perform(get(URI + "/admin?page=1")
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.currentPage").value(1));
    }

    @Test
    void getAllUsersSortTest() throws Exception {
        mockMvc.perform(get(URI + "/admin?sort=id")
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$.content[1].id").value(testAdmin.getId()));
        assertTrue(testAdmin.getId() > testUser.getId(), "Admin ID should be greater than user ID");
    }

    @Test
    void getAllUsersSortDirectionTest() throws Exception {
        mockMvc.perform(get(URI + "/admin?sort=id,DESC")
                        .with(user(testAdmin))
                        .principal(() -> String.valueOf(testAdmin.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(testAdmin.getId()))
                .andExpect(jsonPath("$.content[1].id").value(testUser.getId()));
        assertTrue(testAdmin.getId() > testUser.getId(), "Admin ID should be greater than user ID");
    }


    @Test
    void getAllUsersAsUserTest() throws Exception {
        mockMvc.perform(get(URI + "/admin?sort=id,DESC")
                        .with(user(testUser))
                        .principal(() -> String.valueOf(testUser.getId())))
                .andExpect(status().isForbidden());
    }
}
