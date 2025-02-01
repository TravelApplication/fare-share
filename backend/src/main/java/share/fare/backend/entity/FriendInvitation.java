package share.fare.backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("FRIEND")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class FriendInvitation extends Invitation {
}
