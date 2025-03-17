package share.fare.backend.util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private Long senderId;
    private Long groupId;
    private String message;
}
