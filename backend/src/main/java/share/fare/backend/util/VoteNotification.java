package share.fare.backend.util;

import lombok.*;
import lombok.experimental.SuperBuilder;
import share.fare.backend.entity.VoteType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VoteNotification extends Notification {
    private Long activityId;
    private Long voteId;
    private Long groupId;
    private VoteType voteType;
}
