package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class GroupMembershipResponseDto {
    private Long id;
    private Long groupId;
    private Long userId;
    private String role;
    private LocalDate joinedAt;
}