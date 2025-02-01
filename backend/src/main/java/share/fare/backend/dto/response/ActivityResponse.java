package share.fare.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String link;
    private Long groupId;
    private LocalDateTime createdAt;
    private List<VoteResponse> votes;
}
