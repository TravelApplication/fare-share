package share.fare.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("GROUP")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitation extends Invitation {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    protected Group group;
}
