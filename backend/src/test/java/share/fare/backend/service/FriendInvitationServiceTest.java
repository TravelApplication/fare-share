package share.fare.backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.response.FriendInvitationResponse;
import share.fare.backend.entity.FriendInvitation;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.User;
import share.fare.backend.exception.InvitationAlreadyExistsException;
import share.fare.backend.exception.InvitationNotFoundException;
import share.fare.backend.exception.UserNotFoundException;
import share.fare.backend.repository.FriendInvitationRepository;
import share.fare.backend.repository.FriendshipRepository;
import share.fare.backend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendInvitationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendInvitationRepository invitationRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendInvitationService friendInvitationService;

    private User testSender;
    private User testReceiver;
    private FriendInvitation testInvitation;
    private Friendship testFriendship;

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

        testInvitation = FriendInvitation.builder()
                .id(1L)
                .sender(testSender)
                .receiver(testReceiver)
                .build();

        testFriendship = Friendship.builder()
                .id(new FriendshipId(1L, 2L))
                .user1(testSender)
                .user2(testReceiver)
                .build();
    }

    @Test
    @Transactional
    public void testSendFriendInvitationSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testReceiver));
        when(invitationRepository.existsBySenderIdAndReceiverId(1L, 2L)).thenReturn(false);
        when(invitationRepository.save(any(FriendInvitation.class))).thenReturn(testInvitation);

        FriendInvitationResponse result = friendInvitationService.sendFriendInvitation(1L, 2L);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(invitationRepository, times(1)).existsBySenderIdAndReceiverId(1L, 2L);
        verify(invitationRepository, times(1)).save(any(FriendInvitation.class));
    }

    @Test
    @Transactional
    public void testSendFriendInvitationSenderEqualsReceiver() {
        assertThrows(IllegalArgumentException.class, () -> friendInvitationService.sendFriendInvitation(1L, 1L));
    }

    @Test
    @Transactional
    public void testSendFriendInvitationInvitationAlreadyExists() {
        when(invitationRepository.existsBySenderIdAndReceiverId(1L, 2L)).thenReturn(true);

        assertThrows(InvitationAlreadyExistsException.class, () -> friendInvitationService.sendFriendInvitation(1L, 2L));
        verify(invitationRepository, times(1)).existsBySenderIdAndReceiverId(1L, 2L);
    }

    @Test
    @Transactional
    public void testSendFriendInvitationSenderNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> friendInvitationService.sendFriendInvitation(1L, 2L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testSendFriendInvitationReceiverNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testSender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> friendInvitationService.sendFriendInvitation(1L, 2L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    public void testGetSentFriendInvitationsSuccess() {
        when(invitationRepository.findBySenderId(1L)).thenReturn(Collections.singletonList(testInvitation));

        List<FriendInvitationResponse> result = friendInvitationService.getSentFriendInvitations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(invitationRepository, times(1)).findBySenderId(1L);
    }

    @Test
    public void testGetReceivedFriendInvitationsSuccess() {
        when(invitationRepository.findByReceiverId(2L)).thenReturn(Collections.singletonList(testInvitation));

        List<FriendInvitationResponse> result = friendInvitationService.getReceivedFriendInvitations(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(invitationRepository, times(1)).findByReceiverId(2L);
    }

    @Test
    @Transactional
    public void testAcceptFriendInvitationSuccess() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(testFriendship);
        doNothing().when(invitationRepository).delete(testInvitation);

        friendInvitationService.acceptFriendInvitation(1L, 2L);

        verify(invitationRepository, times(1)).findById(1L);
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
        verify(invitationRepository, times(1)).delete(testInvitation);
    }

    @Test
    @Transactional
    public void testAcceptFriendInvitationInvitationNotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvitationNotFoundException.class, () -> friendInvitationService.acceptFriendInvitation(1L, 2L));
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testAcceptFriendInvitationUserNotReceiver() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));

        assertThrows(InvitationNotFoundException.class, () -> friendInvitationService.acceptFriendInvitation(1L, 3L));
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testRejectFriendInvitationSuccess() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));
        doNothing().when(invitationRepository).delete(testInvitation);

        friendInvitationService.rejectFriendInvitation(1L, 2L);

        verify(invitationRepository, times(1)).findById(1L);
        verify(invitationRepository, times(1)).delete(testInvitation);
    }

    @Test
    @Transactional
    public void testRejectFriendInvitationInvitationNotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvitationNotFoundException.class, () -> friendInvitationService.rejectFriendInvitation(1L, 2L));
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    public void testRejectFriendInvitationUserNotReceiver() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));

        assertThrows(InvitationNotFoundException.class, () -> friendInvitationService.rejectFriendInvitation(1L, 3L));
        verify(invitationRepository, times(1)).findById(1L);
    }
}