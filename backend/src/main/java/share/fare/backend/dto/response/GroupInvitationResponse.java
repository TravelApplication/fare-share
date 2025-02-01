package share.fare.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class GroupInvitationResponse extends InvitationResponse {
    private Long groupId;
}

