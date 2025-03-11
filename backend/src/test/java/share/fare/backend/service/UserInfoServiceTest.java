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
import share.fare.backend.dto.request.UserInfoRequest;
import share.fare.backend.dto.response.UserInfoResponse;
import share.fare.backend.entity.Role;
import share.fare.backend.entity.User;
import share.fare.backend.entity.UserInfo;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.UserInfoRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {
    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    private User testUser;
    private UserInfo testUserInfo;
    private UserInfoRequest userInfoRequest;
    private List<UserInfo> users;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .role(Role.USER)
                .build();

        testUserInfo = UserInfo.builder()
                .id(1L)
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .bio("Test bio")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("+123456789")
                .build();

        testUser.setUserInfo(testUserInfo);

        userInfoRequest = UserInfoRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .bio("Updated bio")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .phoneNumber("+987654321")
                .build();

        users = List.of(testUserInfo);
    }

    @Test
    public void testAddOrUpdateUserInfo_NewUserInfo() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userInfoRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userInfoRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfoResponse response = userInfoService.addOrUpdateUserInfo(userInfoRequest, userId);

        assertNotNull(response);
        assertEquals("Updated", response.getFirstName());
        assertEquals("Updated bio", response.getBio());

        verify(userRepository, times(1)).findById(userId);
        verify(userInfoRepository, times(1)).findByUserId(userId);
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

    @Test
    public void testAddOrUpdateUserInfo_ExistingUserInfo() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userInfoRepository.findByUserId(userId)).thenReturn(Optional.of(testUserInfo));
        when(userInfoRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfoResponse response = userInfoService.addOrUpdateUserInfo(userInfoRequest, userId);

        assertNotNull(response);
        assertEquals("Updated", response.getFirstName());
        assertEquals("Updated bio", response.getBio());

        verify(userRepository, times(1)).findById(userId);
        verify(userInfoRepository, times(1)).findByUserId(userId);
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

    @Test
    public void testAddOrUpdateUserInfo_UserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userInfoService.addOrUpdateUserInfo(userInfoRequest, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userInfoRepository, times(0)).findByUserId(any());
        verify(userInfoRepository, times(0)).save(any());
    }

    @Test
    public void testSearchTop8Users() {
        String name = "John";
        when(userInfoRepository.findTop8ByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name))
                .thenReturn(users);

        List<UserInfoResponse> result = userInfoService.searchTop8Users(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());

        verify(userInfoRepository, times(1))
                .findTop8ByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Test
    public void testSearchUsersPaginated() {
        String name = "John";
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserInfo> userPage = new PageImpl<>(users, pageable, users.size());

        when(userInfoRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable))
                .thenReturn(userPage);

        Page<UserInfoResponse> result = userInfoService.searchUsersPaginated(name, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());

        verify(userInfoRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable);
    }

    @Test
    public void testGetUserProfile() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserInfoResponse response = userInfoService.getUserProfile(userId);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserProfileUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userInfoService.getUserProfile(userId));

        verify(userRepository, times(1)).findById(userId);
    }


}