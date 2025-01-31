package share.fare.backend.mapper;

import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupMembership;

public class GroupMembershipMapper {
    public static GroupMembershipResponse toResponse(GroupMembership membership) {
        if (membership == null) {
            return null;
        }

        return GroupMembershipResponse.builder()
                .id(membership.getId())
                .userId(membership.getUser() != null ? membership.getUser().getId() : null)
                .userEmail(membership.getUser() != null ? membership.getUser().getEmail() : null)
                .groupId(membership.getGroup() != null ? membership.getGroup().getId() : null)
                .role(membership.getRole())
                .joinedAt(membership.getJoinedAt())
                .build();
    }

}