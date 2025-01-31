package share.fare.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "user_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime createdAt;

    private LocalDate tripStartDate;
    private LocalDate tripEndDate;

    @ElementCollection
    @CollectionTable(name = "group_tags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private String groupImageUrl;

    @ElementCollection
    @CollectionTable(name = "group_links", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "link")
    private List<String> links = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembership> memberships = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public void addMember(User user, GroupRole role) {
        GroupMembership membership = GroupMembership.builder()
                .user(user)
                .group(this)
                .role(role)
                .joinedAt(LocalDate.now())
                .build();
        memberships.add(membership);
    }
}