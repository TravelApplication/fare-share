package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import share.fare.backend.entity.GroupRole;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GroupMembershipResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long groupId;
    private GroupRole role;
    private LocalDate joinedAt;
}