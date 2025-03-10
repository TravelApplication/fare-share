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
import share.fare.backend.repository.FriendInvitationRepository;
import share.fare.backend.repository.FriendshipRepository;
import share.fare.backend.repository.UserRepository;

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
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(invitationRepository, times(1)).existsBySenderIdAndReceiverId(1L, 2L);
        verify(invitationRepository, times(1)).save(any(FriendInvitation.class));
    }

}