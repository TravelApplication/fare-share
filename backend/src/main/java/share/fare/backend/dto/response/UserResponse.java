package share.fare.backend.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private UserInfoResponse userInfo;
    private List<GroupResponse> groupsCreated;
    private List<GroupMembershipResponse> memberships;
}