package share.fare.backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.response.GroupInvitationResponse;
import share.fare.backend.entity.*;
import share.fare.backend.exception.*;
import share.fare.backend.repository.GroupInvitationRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupInvitationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupInvitationRepository invitationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private GroupInvitationService groupInvitationService;

    private User testSender;
    private User testReceiver;
    private Group testGroup;
    private GroupInvitation testInvitation;

    @BeforeEach
    public void setUp() {
        testSender = User.builder()
                .id(1L)
                .email("sender@test.com")
                .password("password")
                .build();

        testReceiver = User.builder()
                .id(2L)
                .email("receiver@test.com")
                .password("password")
                .build();

        testGroup = Group.builder()
                .id(1L)
                .name("Test Group")
                .description("Test Description")
                .memberships(new ArrayList<>())
                .build();

        testGroup.addMember(testSender, GroupRole.OWNER);

        testInvitation = GroupInvitation.builder()
                .id(1L)
                .sender(testSender)
                .receiver(testReceiver)
                .group(testGroup)
                .build();
    }

    @Test
    @Transactional
    public void testSendGroupInvitationSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testReceiver));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(invitationRepository.existsBySenderIdAndReceiverIdAndGroupId(1L, 2L, 1L)).thenReturn(false);
        when(invitationRepository.save(any(GroupInvitation.class))).thenReturn(testInvitation);

        GroupInvitationResponse result = groupInvitationService.sendGroupInvitation(1L, 2L, 1L);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(groupRepository, times(1)).findById(1L);
        verify(invitationRepository, times(1)).existsBySenderIdAndReceiverIdAndGroupId(1L, 2L, 1L);
        verify(invitationRepository, times(1)).save(any(GroupInvitation.class));
        verify(notificationService).sendNotificationToUser(eq(2L), argThat(notification -> notification.getSenderId() == 1L));
    }

    @Test
    @Transactional
    public void testSendGroupInvitationSenderEqualsReceiver() {
        assertThrows(ActionIsNotAllowedException.class, () -> groupInvitationService.sendGroupInvitation(1L, 1L, 1L));
    }

    @Test
    @Transactional
    public void testSendGroupInvitationInvitationAlreadyExists() {
        when(invitationRepository.existsBySenderIdAndReceiverIdAndGroupId(1L, 2L, 1L)).thenReturn(true);

        assertThrows(InvitationAlreadyExistsException.class, () -> groupInvitationService.sendGroupInvitation(1L, 2L, 1L));
        verify(invitationRepository, times(1)).existsBySenderIdAndReceiverIdAndGroupId(1L, 2L, 1L);
    }

    @Test
    @Transactional
    public void testSendGroupInvitationSenderNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> groupInvitationService.sendGroupInvitation(1L, 2L, 1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testSendGroupInvitationReceiverNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        assertThrows(UserNotFoundException.class, () -> groupInvitationService.sendGroupInvitation(1L, 2L, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    @Transactional
    public void testSendGroupInvitationGroupNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSender));
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupInvitationService.sendGroupInvitation(1L, 2L, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetSentGroupInvitationsSuccess() {
        when(invitationRepository.findBySenderId(1L)).thenReturn(Collections.singletonList(testInvitation));

        List<GroupInvitationResponse> result = groupInvitationService.getSentGroupInvitations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(invitationRepository, times(1)).findBySenderId(1L);
    }

    @Test
    public void testGetReceivedGroupInvitationsSuccess() {
        when(invitationRepository.findByReceiverId(2L)).thenReturn(Collections.singletonList(testInvitation));

        List<GroupInvitationResponse> result = groupInvitationService.getReceivedGroupInvitations(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(invitationRepository, times(1)).findByReceiverId(2L);
    }

    @Test
    @Transactional
    public void testAcceptGroupInvitationSuccess() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);
        doNothing().when(invitationRepository).deleteAllByGroupIdAndReceiverId(1L, 2L);
        doNothing().when(invitationRepository).delete(testInvitation);

        groupInvitationService.acceptGroupInvitation(1L, 2L);

        verify(invitationRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(invitationRepository, times(1)).deleteAllByGroupIdAndReceiverId(1L, 2L);
        verify(invitationRepository, times(1)).delete(testInvitation);
        verify(notificationService).sendNotificationToUser(eq(1L), argThat(notification -> notification.getSenderId() == 2L));
    }

    @Test
    @Transactional
    public void testAcceptGroupInvitationInvitationNotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvitationNotFoundException.class, () -> groupInvitationService.acceptGroupInvitation(1L, 2L));
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testAcceptGroupInvitationUserNotReceiver() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));

        assertThrows(ActionIsNotAllowedException.class, () -> groupInvitationService.acceptGroupInvitation(1L, 3L));
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testRejectGroupInvitationSuccess() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));
        doNothing().when(invitationRepository).delete(testInvitation);

        groupInvitationService.rejectGroupInvitation(1L, 2L);

        verify(invitationRepository, times(1)).findById(1L);
        verify(invitationRepository, times(1)).delete(testInvitation);
        verify(notificationService).sendNotificationToUser(eq(1L), argThat(notification -> notification.getSenderId() == 2L));
    }

    @Test
    @Transactional
    public void testRejectGroupInvitationInvitationNotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvitationNotFoundException.class, () -> groupInvitationService.rejectGroupInvitation(1L, 2L));
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testRejectGroupInvitationUserNotReceiver() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));

        assertThrows(ActionIsNotAllowedException.class, () -> groupInvitationService.rejectGroupInvitation(1L, 3L));
        verify(invitationRepository, times(1)).findById(1L);
    }
}