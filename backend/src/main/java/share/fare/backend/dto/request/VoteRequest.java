package share.fare.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import share.fare.backend.entity.VoteType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {
    @NotNull(message = "Vote type is required")
    private VoteType voteType;
}