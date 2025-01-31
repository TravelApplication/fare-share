package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private LocalDate createdAt;
    private UserInfoResponse userInfo;
    private List<GroupResponse> groupsCreated;
    private List<GroupMembershipResponse> memberships;
}