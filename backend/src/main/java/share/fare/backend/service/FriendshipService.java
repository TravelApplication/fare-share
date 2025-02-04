package share.fare.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.fare.backend.dto.response.UserGeneralResponse;
import share.fare.backend.entity.Friendship;
import share.fare.backend.entity.FriendshipId;
import share.fare.backend.entity.User;
import share.fare.backend.exception.FriendshipNotFoundException;
import share.fare.backend.mapper.UserMapper;
import share.fare.backend.repository.FriendshipRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public List<UserGeneralResponse> findFriendsByUserId(Long userId) {
        List<User> friends = friendshipRepository.findFriendsByUserId(userId);
        return friends.stream()
                .map(UserMapper::toGeneralResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFriendship(Long user1Id, Long user2Id) {
        Friendship friendship = friendshipRepository.findById(new FriendshipId(user1Id, user2Id))
                .orElseThrow(FriendshipNotFoundException::new);
        friendshipRepository.delete(friendship);
    }
}