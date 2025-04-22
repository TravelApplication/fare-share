package share.fare.backend.util;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Notification {
    private NotificationType type;
    private Long senderId;
    private String message;
}
