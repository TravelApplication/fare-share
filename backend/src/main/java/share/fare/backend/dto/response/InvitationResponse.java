package share.fare.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
public class InvitationResponse {
    private Long id;
    private UserGeneralResponse sender;
    private UserGeneralResponse receiver;
    private LocalDateTime createdAt;
}