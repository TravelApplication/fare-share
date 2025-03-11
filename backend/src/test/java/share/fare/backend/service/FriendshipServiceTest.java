package share.fare.backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import share.fare.backend.dto.response.UserGeneralResponse;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.User;
import share.fare.backend.exception.FriendshipNotFoundException;
import share.fare.backend.repository.FriendshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private User testUser1;
    private User testUser2;
    private Friendship testFriendship;

    @BeforeEach
    public void setUp() {
        testUser1 = User.builder()
                .id(1L)
                .email("user1@test.com")
                .password("password1")
                .build();

        testUser2 = User.builder()
                .id(2L)
                .email("user2@test.com")
                .password("password2")
                .build();

        testFriendship = Friendship.builder()
                .id(new FriendshipId(1L, 2L))
                .user1(testUser1)
                .user2(testUser2)
                .build();
    }

    @Test
    public void testFindFriendsByUserIdSuccess() {
        when(friendshipRepository.findFriendsByUserId(1L)).thenReturn(Collections.singletonList(testUser2));

        List<UserGeneralResponse> result = friendshipService.findFriendsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user2@test.com", result.getFirst().getEmail());
        verify(friendshipRepository, times(1)).findFriendsByUserId(1L);
    }

    @Test
    public void testFindFriendsByUserIdNoFriends() {
        when(friendshipRepository.findFriendsByUserId(1L)).thenReturn(Collections.emptyList());

        List<UserGeneralResponse> result = friendshipService.findFriendsByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(friendshipRepository, times(1)).findFriendsByUserId(1L);
    }

    @Test
    @Transactional
    public void testDeleteFriendshipSuccess() {
        when(friendshipRepository.findById(new FriendshipId(1L, 2L))).thenReturn(Optional.of(testFriendship));
        doNothing().when(friendshipRepository).delete(testFriendship);

        friendshipService.deleteFriendship(1L, 2L);

        verify(friendshipRepository, times(1)).findById(new FriendshipId(1L, 2L));
        verify(friendshipRepository, times(1)).delete(testFriendship);
    }

    @Test
    @Transactional
    public void testDeleteFriendshipNotFound() {
        when(friendshipRepository.findById(new FriendshipId(1L, 2L))).thenReturn(Optional.empty());

        assertThrows(FriendshipNotFoundException.class, () -> friendshipService.deleteFriendship(1L, 2L));
        verify(friendshipRepository, times(1)).findById(new FriendshipId(1L, 2L));
    }
}