package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserInfoResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private LocalDate dateOfBirth;
}
