package share.fare.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRequest {
    @NotBlank(message = "Activity name is required")
    private String name;

    private String description;
    private String location;

    @URL(message = "Link must be a valid URL")
    private String link;

    @FutureOrPresent
    private LocalDate startDate;

    @Future
    private LocalDate endDate;
}