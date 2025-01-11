package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private LocalDate createdAt;
    private UserInfoResponseDto userInfo;
    private List<GroupResponseDto> groupsCreated;
    private List<GroupMembershipResponseDto> memberships;
}