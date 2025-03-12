package share.fare.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import share.fare.backend.util.Notification;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/notifications",
                notification
        );
    }
}