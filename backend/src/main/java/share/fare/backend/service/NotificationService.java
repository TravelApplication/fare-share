package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import share.fare.backend.util.Notification;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(Long userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/notifications",
                notification
        );
    }

    public void sendNotificationToGroup(Long groupId, Notification notification) {
        messagingTemplate.convertAndSend("/group/" + groupId + "/notifications", notification);
    }
}