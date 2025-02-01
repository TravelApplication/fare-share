package share.fare.backend.mapper;

import share.fare.backend.dto.response.GroupInvitationResponse;
import share.fare.backend.entity.GroupInvitation;

public class GroupInvitationMapper {
    public static GroupInvitationResponse toResponse(GroupInvitation invitation) {
        return GroupInvitationResponse.builder()
                .id(invitation.getId())
                .senderId(invitation.getSender().getId())
                .receiverId(invitation.getReceiver().getId())
                .groupId(invitation.getGroup().getId())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
