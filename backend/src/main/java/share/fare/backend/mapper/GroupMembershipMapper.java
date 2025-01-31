package share.fare.backend.mapper;

import share.fare.backend.dto.response.GroupMembershipResponse;
import share.fare.backend.entity.GroupMembership;

public class GroupMembershipMapper {
    public static GroupMembershipResponse toResponse(GroupMembership groupMembership) {
        return GroupMembershipResponse.builder()
                .id(groupMembership.getId())
                .groupId(groupMembership.getGroup().getId())
                .userId(groupMembership.getUser().getId())
                .role(String.valueOf(groupMembership.getRole()))
                .joinedAt(groupMembership.getJoinedAt())
                .build();
    }
}