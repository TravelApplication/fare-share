package share.fare.backend.mapper;

import share.fare.backend.dto.response.FriendInvitationResponse;
import share.fare.backend.entity.FriendInvitation;

public class FriendInvitationMapper {
    public static FriendInvitationResponse toResponse(FriendInvitation invitation) {
        return FriendInvitationResponse.builder()
                .id(invitation.getId())
                .senderId(invitation.getSender().getId())
                .receiverId(invitation.getReceiver().getId())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
