package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import share.fare.backend.dto.request.ActivityRequest;
import share.fare.backend.dto.response.ActivityResponse;
import share.fare.backend.entity.Activity;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.User;
import share.fare.backend.exception.ActivityNotFoundException;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.ActivityRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.repository.VoteRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ActivityService activityService;

    private User testUser;
    private Group testGroup;
    private Activity testActivity;
    private ActivityRequest testActivityRequest;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .build();

        testGroup = Group.builder()
                .id(1L)
                .name("Test Group")
                .description("Test Description")
                .build();

        testActivity = Activity.builder()
                .id(1L)
                .name("Test Activity")
                .description("Test Description")
                .link("https://example.com")
                .group(testGroup)
                .build();

        testActivityRequest = ActivityRequest.builder()
                .name("Test Activity")
                .description("Test Description")
                .link("https://example.com")
                .build();
    }

    @Test
    public void testCreateActivitySuccess() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        ActivityResponse result = activityService.createActivity(testActivityRequest, 1L, 1L);

        assertNotNull(result);
        assertEquals("Test Activity", result.getName());
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    public void testCreateActivityGroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> activityService.createActivity(testActivityRequest, 1L, 1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateActivityUserNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> activityService.createActivity(testActivityRequest, 1L, 1L));
        verify(groupRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateActivitySuccess() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        ActivityResponse result = activityService.updateActivity(testActivityRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Activity", result.getName());
        verify(activityRepository, times(1)).findById(1L);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    public void testUpdateActivityActivityNotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ActivityNotFoundException.class, () -> activityService.updateActivity(testActivityRequest, 1L));
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteActivitySuccess() {
        doNothing().when(activityRepository).deleteById(1L);

        activityService.deleteActivity(1L);

        verify(activityRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetActivitiesForGroupSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Activity> activityPage = new PageImpl<>(Collections.singletonList(testActivity), pageable, 1);
        when(activityRepository.findByGroup_Id(1L, pageable)).thenReturn(activityPage);

        Page<ActivityResponse> result = activityService.getActivitiesForGroup(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Activity", result.getContent().getFirst().getName());
        verify(activityRepository, times(1)).findByGroup_Id(1L, pageable);
    }

}