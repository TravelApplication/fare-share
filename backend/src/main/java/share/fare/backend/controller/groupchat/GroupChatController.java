package share.fare.backend.controller.groupchat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.entity.ChatMessage;
import share.fare.backend.repository.ChatMessageRepository;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupChatController {

    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/{groupId}/chat")
    public Page<ChatMessage> getRecentMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chatMessageRepository.findByGroupIdOrderByTimestampDesc(groupId, PageRequest.of(page, size));
    }
}