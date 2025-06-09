package share.fare.backend.mapper;

import share.fare.backend.dto.response.FriendInvitationResponse;
import share.fare.backend.entity.FriendInvitation;

public class FriendInvitationMapper {

    public static FriendInvitationResponse toResponse(FriendInvitation invitation) {
        return FriendInvitationResponse.builder()
                .id(invitation.getId())
                .sender(UserMapper.toGeneralResponse(invitation.getSender()))
                .receiver(UserMapper.toGeneralResponse(invitation.getReceiver()))
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
