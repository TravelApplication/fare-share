package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import share.fare.backend.entity.GroupInvitation;

import java.util.List;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {
    boolean existsBySenderIdAndReceiverIdAndGroupId(Long senderId, Long receiverId, Long groupId);

    List<GroupInvitation> findBySenderId(Long senderId);

    List<GroupInvitation> findByReceiverId(Long receiverId);

    void deleteAllByGroupIdAndReceiverId(Long groupId, Long receiverId);
}
