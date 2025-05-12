package share.fare.backend.dto.response;

import lombok.*;
import share.fare.backend.entity.VoteType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long activityId;
    private VoteType voteType;
}
