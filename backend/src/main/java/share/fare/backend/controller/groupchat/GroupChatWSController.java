package share.fare.backend.controller.groupchat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import share.fare.backend.entity.User;
import share.fare.backend.entity.ChatMessage;
import share.fare.backend.repository.ChatMessageRepository;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class GroupChatWSController {
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/group/{groupId}/chat.sendMessage")
    @SendTo("/group/{groupId}/chat")
    public ChatMessage sendMessage(@Payload String message,  @DestinationVariable String groupId, StompHeaderAccessor headerAccessor) {
        Authentication authentication = (Authentication) headerAccessor.getSessionAttributes().get("authentication");
        User user = (User) authentication.getPrincipal();
        ChatMessage chatMessage = ChatMessage.builder()
                .content(message)
                .senderId(user.getId())
                .senderEmail(user.getEmail())
                .groupId(Long.valueOf(groupId))
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        return chatMessage;
    }
}
