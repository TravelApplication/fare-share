package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.FriendInvitation;

import java.util.List;

public interface FriendInvitationRepository extends JpaRepository<FriendInvitation, Long> {
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<FriendInvitation> findBySenderId(Long userId);

    List<FriendInvitation> findByReceiverId(Long userId);
}
