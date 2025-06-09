package share.fare.backend.util;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GroupInvitationNotification extends Notification {
    private Long groupId;
    private String groupName;
}
