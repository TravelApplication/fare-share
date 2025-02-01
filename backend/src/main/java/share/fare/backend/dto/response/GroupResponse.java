package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private Long createdByUserId;
    private LocalDateTime createdAt;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private List<String> tags;
    private String groupImageUrl;
    private List<String> links;
    private List<GroupMembershipResponse> memberships;
    private List<ActivityResponse> activities;
}