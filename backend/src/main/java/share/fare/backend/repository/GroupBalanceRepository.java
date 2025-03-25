package share.fare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import share.fare.backend.entity.Group;
import share.fare.backend.entity.GroupBalance;
import share.fare.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface GroupBalanceRepository extends JpaRepository<GroupBalance, Long> {
    Optional<GroupBalance> findByGroupAndUser(Group group, User paidByUser);

    List<GroupBalance> findByGroup(Group group);

    @Query("SELECT gb FROM group_balance gb " +
            "JOIN FETCH gb.user u " +
            "WHERE gb.group.id = :groupId AND u.id IN (:userIds)")
    List<GroupBalance> findWithUsersByGroupAndUserIds(
            @Param("groupId") Long groupId,
            @Param("userIds") List<Long> userIds);
}
