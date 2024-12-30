package share.fare.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long createdById;
}
