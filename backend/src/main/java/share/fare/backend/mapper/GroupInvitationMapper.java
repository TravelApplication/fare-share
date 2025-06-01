package share.fare.backend.mapper;

import share.fare.backend.dto.response.GroupInvitationResponse;
import share.fare.backend.entity.GroupInvitation;

public class GroupInvitationMapper {
    public static GroupInvitationResponse toResponse(GroupInvitation invitation) {
        return GroupInvitationResponse.builder()
                .id(invitation.getId())
                .sender(UserMapper.toGeneralResponse(invitation.getSender()))
                .receiver(UserMapper.toGeneralResponse(invitation.getReceiver()))
                .groupId(invitation.getGroup().getId())
                .groupName(invitation.getGroup().getName())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
