package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserGeneralResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
