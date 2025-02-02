package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.exception.FriendshipNotFoundException;
import share.fare.backend.repository.FriendshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public List<Long> getAllFriendships(Long userId) {
        return friendshipRepository.findFriendIdsByUserId(userId);
    }

    @Transactional
    public void deleteFriendship(Long user1Id, Long user2Id) {
        Friendship friendship = friendshipRepository.findById(new FriendshipId(user1Id, user2Id))
                .orElseThrow(FriendshipNotFoundException::new);
        friendshipRepository.delete(friendship);
    }
}