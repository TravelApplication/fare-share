package share.fare.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.request.VoteRequest;
import share.fare.backend.dto.response.VoteResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.*;
import share.fare.backend.repository.ActivityRepository;
import share.fare.backend.repository.GroupMembershipRepository;
import share.fare.backend.repository.UserRepository;
import share.fare.backend.repository.VoteRepository;
import share.fare.backend.util.NotificationType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VoteService voteService;

    private User testUser;
    private Activity testActivity;
    private Vote testVote;
    private VoteRequest testVoteRequest;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .build();

        testActivity = Activity.builder()
                .id(1L)
                .name("Test Activity")
                .group(Group.builder().id(1L).build())
                .build();

        testVote = Vote.builder()
                .id(1L)
                .voteType(VoteType.FOR)
                .activity(testActivity)
                .user(testUser)
                .build();

        testVoteRequest = VoteRequest.builder()
                .voteType(VoteType.AGAINST)
                .build();
    }

    @Test
    public void testAddVoteSuccess() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voteRepository.existsByActivityAndUser(testActivity, testUser)).thenReturn(false);
        when(groupMembershipRepository.existsByGroupAndUser(testActivity.getGroup(), testUser)).thenReturn(true);
        when(voteRepository.save(any(Vote.class))).thenReturn(testVote);

        VoteResponse result = voteService.addVote(1L, 1L, testVoteRequest);

        assertNotNull(result);
        assertEquals(VoteType.FOR, result.getVoteType());
        verify(activityRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).existsByActivityAndUser(testActivity, testUser);
        verify(groupMembershipRepository, times(1)).existsByGroupAndUser(testActivity.getGroup(), testUser);
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(notificationService).sendNotificationToGroup(anyLong(), argThat(notification ->
            notification.getSenderId() == 1L
        ));
    }

    @Test
    public void testAddVote_ActivityNotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ActivityNotFoundException.class, () -> voteService.addVote(1L, 1L, testVoteRequest));
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddVoteUserNotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> voteService.addVote(1L, 1L, testVoteRequest));
        verify(activityRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddVoteDuplicateVote() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voteRepository.existsByActivityAndUser(testActivity, testUser)).thenReturn(true);

        assertThrows(DuplicateVoteException.class, () -> voteService.addVote(1L, 1L, testVoteRequest));
        verify(activityRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).existsByActivityAndUser(testActivity, testUser);
    }

    @Test
    public void testAddVoteUserNotInGroup() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(voteRepository.existsByActivityAndUser(testActivity, testUser)).thenReturn(false);
        when(groupMembershipRepository.existsByGroupAndUser(testActivity.getGroup(), testUser)).thenReturn(false);

        assertThrows(UserIsNotInGroupException.class, () -> voteService.addVote(1L, 1L, testVoteRequest));
        verify(activityRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).existsByActivityAndUser(testActivity, testUser);
        verify(groupMembershipRepository, times(1)).existsByGroupAndUser(testActivity.getGroup(), testUser);
    }

    @Test
    public void testGetVotesForActivitySuccess() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(voteRepository.findByActivity(testActivity)).thenReturn(Optional.of(testVote));

        List<VoteResponse> result = voteService.getVotesForActivity(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VoteType.FOR, result.getFirst().getVoteType());
        verify(activityRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).findByActivity(testActivity);
    }

    @Test
    public void testGetVotesForActivityActivityNotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ActivityNotFoundException.class, () -> voteService.getVotesForActivity(1L));
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateVoteSuccess() {
        when(voteRepository.findById(1L)).thenReturn(Optional.of(testVote));
        when(voteRepository.save(any(Vote.class))).thenReturn(testVote);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        VoteResponse result = voteService.updateVote(1L, 1L, testVoteRequest);

        assertNotNull(result);
        assertEquals(VoteType.AGAINST, result.getVoteType());
        verify(voteRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(notificationService).sendNotificationToGroup(anyLong(), argThat(notification ->
                notification.getSenderId() == 1L));
    }


    @Test
    public void testUpdateVoteVoteNotFound() {
        when(voteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(VoteNotFoundException.class, () -> voteService.updateVote(1L, 1L, testVoteRequest));
        verify(voteRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateVoteUnauthorizedUser() {
        when(voteRepository.findById(1L)).thenReturn(Optional.of(testVote));
        when(userRepository.findById(2L)).thenThrow(ActionIsNotAllowedException.class);

        assertThrows(ActionIsNotAllowedException.class, () -> voteService.updateVote(1L, 2L, testVoteRequest));
        verify(voteRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteVoteSuccess() {
        when(voteRepository.findById(1L)).thenReturn(Optional.of(testVote));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(voteRepository).delete(testVote);

        voteService.deleteVote(1L, 1L);

        verify(voteRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).delete(testVote);
    }

    @Test
    public void testDeleteVoteVoteNotFound() {
        when(voteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(VoteNotFoundException.class, () -> voteService.deleteVote(1L, 1L));
        verify(voteRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteVote_UnauthorizedUser() {
        when(voteRepository.findById(1L)).thenReturn(Optional.of(testVote));
        when(userRepository.findById(2L)).thenThrow(ActionIsNotAllowedException.class);

        assertThrows(ActionIsNotAllowedException.class, () -> voteService.deleteVote(1L, 2L));
        verify(voteRepository, times(1)).findById(1L);
    }
}