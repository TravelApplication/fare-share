package share.fare.backend.mapping;

import share.fare.backend.dto.response.GroupMembershipResponseDto;
import share.fare.backend.entity.GroupMembership;

public class GroupMembershipMapper {
    public static GroupMembershipResponseDto toResponseDto(GroupMembership groupMembership) {
        return GroupMembershipResponseDto.builder()
                .id(groupMembership.getId())
                .groupId(groupMembership.getGroup().getId())
                .userId(groupMembership.getUser().getId())
                .role(String.valueOf(groupMembership.getRole()))
                .joinedAt(groupMembership.getJoinedAt())
                .build();
    }
}