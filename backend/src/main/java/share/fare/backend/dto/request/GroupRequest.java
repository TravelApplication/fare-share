package share.fare.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Created by user ID is required")
    private Long createdByUserId; // ID of the user creating the group

    @NotEmpty(message = "At least one member must be added to the group")
    private List<Long> memberIds; // List of user IDs to add as members

    @FutureOrPresent(message = "Trip start date must be in the present or future")
    private LocalDate tripStartDate;

    @Future(message = "Trip end date must be in the future")
    private LocalDate tripEndDate;

    @Size(max = 10, message = "A group can have a maximum of 10 tags")
    private List<@NotBlank String> tags;

    @URL(message = "Group image URL must be valid")
    private String groupImageUrl;

    @Size(max = 5, message = "A group can have a maximum of 5 links")
    private List<@URL String> links;
}