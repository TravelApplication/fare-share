package share.fare.backend.controller.groupchat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import share.fare.backend.entity.ChatMessage;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.User;
import share.fare.backend.exception.GroupNotFoundException;
import share.fare.backend.repository.ChatMessageRepository;
import share.fare.backend.repository.GroupRepository;
import share.fare.backend.service.SecurityService;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final GroupRepository groupRepository;
    private final SecurityService securityService;

    @GetMapping("/{groupId}/chat")
    public Page<ChatMessage> getRecentMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user
    ) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        securityService.checkIfUserIsInGroup(user, group);

        return chatMessageRepository.findByGroupIdOrderByTimestampDesc(groupId, PageRequest.of(page, size));
    }
}