package share.fare.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "user_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    private LocalDate createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserInfo userInfo;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Group> groupsCreated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GroupMembership> memberships;
}