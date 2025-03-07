package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import share.fare.backend.dto.request.UserRequest;
import share.fare.backend.dto.response.UserResponse;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    User testUser;
    List<User> users;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .role(Role.USER)
                .email("test@test.com")
                .password("password")
                .id(1L)
                .build();

        users = List.of(testUser);
    }

    @Test
    public void testGetUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, 0);
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponse> result = userService.getUsers(pageable);

        assertNotNull(result);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getUser(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUser_NotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUpdateUser() {
        Long userId = 1L;
        UserRequest userRequest = UserRequest.builder()
                .email("new@example.com")
                .password("newPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);

        UserResponse result = userService.updateUser(userId, userRequest);

        assertNotNull(result);
        assertEquals("new@example.com", testUser.getEmail());
        assertEquals("encodedPassword", testUser.getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void testUpdateUserNotFound() {
        Long userId = 999L;
        UserRequest userRequest = UserRequest.builder()
                .email("new@example.com")
                .password("newPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userRequest));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}