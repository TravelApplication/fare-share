package share.fare.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import share.fare.backend.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByGroupIdOrderByTimestampDesc(Long groupId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.groupId = :groupId")
    void deleteAllByGroupId(Long groupId);
}
